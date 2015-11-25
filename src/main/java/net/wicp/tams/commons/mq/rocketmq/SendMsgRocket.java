package net.wicp.tams.commons.mq.rocketmq;

import java.net.InetAddress;

import org.slf4j.Logger;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.mq.rabbitmq.ConnectionObj;

public class SendMsgRocket {
	private final static Logger logger = LogHelp.getLogger(SendMsgRocket.class);
	private static volatile SendMsgRocket INSTANCE;
	private static Object lockobj = new Object();
	private DefaultMQProducer producer;

	public static final SendMsgRocket getInstance() {
		if (INSTANCE == null) {
			synchronized (lockobj) {
				if (INSTANCE == null) {
					SendMsgRocket tempobj = new SendMsgRocket();
					try {
						tempobj.producer = new DefaultMQProducer(group);
						tempobj.producer.setInstanceName("MSG_SENDER-" + InetAddress.getLocalHost().getHostName());
						tempobj.producer.setNamesrvAddr(nameSrvAddr);
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

	public void destroy() {
		if (producer != null) {
			producer.shutdown();
			logger.info("[{}] metaq 发送端关闭成功.", nameSrvAddr);
		}
	}

	public SendResult SendMsg(String msg, String doctype) {
		Message sendMsg = new Message(topic, doctype, msg.getBytes());
		try {
			SendResult sendResult = producer.send(sendMsg);
			return sendResult;
		} catch (Exception e) {
			logger.error(String.format("发送消息失败,消息：[{}]", msg), e);
			return null;
		}
	}

	public SendResult SendMsg(String msg) {
		return SendMsg(msg, defaultTag);
	}

}
