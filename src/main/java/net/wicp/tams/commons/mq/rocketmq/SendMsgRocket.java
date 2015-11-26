package net.wicp.tams.commons.mq.rocketmq;

import java.net.InetAddress;
import java.util.Properties;

import org.slf4j.Logger;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;

public class SendMsgRocket {
	private final static Logger logger = LogHelp.getLogger(SendMsgRocket.class);
	private static volatile SendMsgRocket INSTANCE;
	private static Object lockobj = new Object();
	private DefaultMQProducer producer;

	static {
		Conf.addCallBack("rocket", new Conf.Callback() {
			@Override
			public void doReshConf(Properties newProperties) {
				destroy();
				INSTANCE = null;
			}
		}, "rocketmq.server.%s");
	}

	public static final SendMsgRocket getInstance() {
		if (INSTANCE == null) {
			synchronized (lockobj) {
				if (INSTANCE == null) {
					SendMsgRocket tempobj = new SendMsgRocket();
					try {
						tempobj.producer = new DefaultMQProducer(Conf.get("rocketmq.server.defaultgroup"));// 组，一个项目一般只发到一个组里
						tempobj.producer.setInstanceName("MSG_SENDER-" + InetAddress.getLocalHost().getHostName());
						tempobj.producer.setNamesrvAddr(Conf.get("rocketmq.server.namesrvaddr"));// 地址
						tempobj.producer.start();
						logger.info("MQ发送端启动成功");
						INSTANCE = tempobj;
					} catch (Exception e) {
						logger.error("MQ发送端启动失败", e);
						e.printStackTrace();
					}

				}
			}
		}
		return INSTANCE;
	}

	private static void destroy() {
		if (SendMsgRocket.INSTANCE != null && SendMsgRocket.INSTANCE.producer != null) {
			SendMsgRocket.INSTANCE.producer.shutdown();
			logger.info("[{}] metaq 发送端关闭成功.", Conf.get("rocketmq.server.namesrvaddr"));
		}
	}

	/***
	 * 发送消息
	 * 
	 * @param topic
	 *            主题
	 * @param tag
	 *            标签
	 * @param msg
	 *            发送的信息
	 * @return 发送结果
	 */
	public SendResult SendMsg(String topic, String tag, String msg) {
		Message sendMsg = new Message(topic, tag, msg.getBytes());
		try {
			SendResult sendResult = producer.send(sendMsg);
			return sendResult;
		} catch (Exception e) {
			logger.error(String.format("发送消息失败,消息：[{}]", msg), e);
			return null;
		}
	}

	/***
	 * 给默认主题（配置文件） 发送消息
	 * 
	 * @param tag
	 *            标签
	 * @param msg
	 *            发送的信息
	 * @return 发送结果
	 */
	public SendResult SendMsg(String tag, String msg) {
		return SendMsg(Conf.get("rocketmq.server.defaulttopic"), tag, msg);
	}

	/***
	 * 给默认主题（配置文件）的默认标签（配置文件）发送消息
	 * 
	 * @param msg
	 *            发送的信息
	 * @return 发送结果
	 */
	public SendResult SendMsg(String msg) {
		return SendMsg(Conf.get("rocketmq.server.defaulttopic"), Conf.get("rocketmq.server.defaulttag"), msg);
	}

}
