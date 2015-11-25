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
	public static final String COMMONS = "NOTAG";// 默认标签
	private static Map<String, List<Observer>> obsmap = new HashMap<>();

	private static final AcceptMsgRabbit INSTANCE = new AcceptMsgRabbit();

	public static AcceptMsgRabbit getInstance() {
		return INSTANCE;
	}

	@Override
	public synchronized void addObserver(Observer observer) {
		addObserver(COMMONS, observer);
	}

	public synchronized void addObserver(String consumerSource, Observer observer) {
		boolean needConsumer = false;// 是否需要构建新消费者
		if (obsmap.get(consumerSource) == null) {
			obsmap.put(consumerSource, new ArrayList<Observer>());
			needConsumer = true;
		}
		addObserver(consumerSource, observer, needConsumer);
	}

	private void messageArrived(String tag, Object message) {
		for (Observer observer : obsmap.get(tag)) {
			observer.update(this, message);
		}
	}

	public synchronized void addObserver(final String consumerSource, Observer observer, boolean needConsumer) {
		obsmap.get(consumerSource).add(observer);
		if (needConsumer) {
			ThreadPool.getDefaultPool().submit(new Runnable() {

				@Override
				public void run() {
					Channel channel = ConnectionObj.getInstance().getChannel();
					try {
						QueueingConsumer consumer = new QueueingConsumer(channel);
						// 默认是需要consumer收到消息后才进行ack
						boolean autoAck = false;
						channel.basicConsume(consumerSource, autoAck, consumer);// TODO
																				// 怎么把consumerSource与Query名对应上
						while (true) {
							QueueingConsumer.Delivery delivery = consumer.nextDelivery(3000);
							if (delivery == null) {
								continue;
							}
							byte[] message = delivery.getBody();

							try {
								messageArrived(consumerSource, new String(message));
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
