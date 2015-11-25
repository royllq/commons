package net.wicp.tams.commons.mq.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.thread.ThreadPool;

/***
 * RabbitMQ的接收者
 * 
 * @author andy.zhou
 *
 */
public class AcceptMsgRabbit extends Observable {
	private final Logger logger = LogHelp.getLogger(getClass());
	public static final String DefaultTQuery = Conf.get("rabbitmq.server.default.queryname");// 默认Query名
	private static Map<String, List<Observer>> obsmap = new HashMap<>();

	private static final AcceptMsgRabbit INSTANCE = new AcceptMsgRabbit();

	public static AcceptMsgRabbit getInstance() {
		return INSTANCE;
	}

	/***
	 * 给默认的Query 增加观察者
	 */
	@Override
	public synchronized void addObserver(Observer observer) {
		addObserver(DefaultTQuery, observer);
	}

	public synchronized void addObserver(String queryName, Observer observer) {
		boolean needConsumer = false;// 是否需要构建新消费者
		if (obsmap.get(queryName) == null) {
			obsmap.put(queryName, new ArrayList<Observer>());
			needConsumer = true;
		}
		addObserver(queryName, observer, needConsumer);
	}

	private void messageArrived(String tag, Object message) {
		for (Observer observer : obsmap.get(tag)) {
			observer.update(this, message);
		}
	}

	public synchronized void addObserver(final String queryName, Observer observer, boolean needConsumer) {
		if (obsmap.get(queryName) == null) {
			obsmap.put(queryName, new ArrayList<Observer>());
		}
		obsmap.get(queryName).add(observer);
		if (needConsumer) {
			ThreadPool.getDefaultPool().submit(new Runnable() {
				@Override
				public void run() {
					Channel channel = ConnectionObj.getInstance().getChannel();
					try {
						QueueingConsumer consumer = new QueueingConsumer(channel);
						// 默认是需要consumer收到消息后才进行ack
						boolean autoAck = false;
						channel.basicConsume(queryName, autoAck, consumer);
						while (true) {
							QueueingConsumer.Delivery delivery = consumer.nextDelivery(3000);
							if (delivery == null) {
								continue;
							}
							byte[] message = delivery.getBody();

							try {
								messageArrived(queryName, new String(message));
								channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
							} catch (Exception e) {
								logger.error("接收消息时失败", e);
							}
						}

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ShutdownSignalException e) {
						ConnectionObj.closeChannelAndConnection();
					} catch (ConsumerCancelledException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						ConnectionObj.closeChannelAndConnection();
					}
				}
			});
		}
	}

}
