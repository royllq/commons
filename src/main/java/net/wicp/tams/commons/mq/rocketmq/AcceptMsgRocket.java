package net.wicp.tams.commons.mq.rocketmq;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;

import net.wicp.tams.commons.LogHelp;

public class AcceptMsgRocket {
	private final static Logger logger = LogHelp.getLogger(AcceptMsgRocket.class);
	private static volatile AcceptMsgRocket INSTANCE;
	private static Object lockobj = new Object();
	private DefaultMQPushConsumer consumer;

	public static final AcceptMsgRocket getInstance() {
		if (INSTANCE == null) {
			synchronized (lockobj) {
				if (INSTANCE == null) {
					AcceptMsgRocket tempobj = new AcceptMsgRocket();
					tempobj.consumer = new DefaultMQPushConsumer();
					tempobj.consumer.setNamesrvAddr(nameSrvAddr);
					try {
						tempobj.consumer.setInstanceName("MSG_ACCEPT-" + InetAddress.getLocalHost().getHostName());
					} catch (UnknownHostException e1) {
						tempobj.consumer.setInstanceName("inst");
					}
					
					/**
					 * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
					 * 如果非第一次启动，那么按照上次消费的位置继续消费
					 */
					consumer.setConsumerGroup(group);
					consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
					if (StringUtils.isBlank(doctype)) {
						doctype = SendMsgService.defaultTag;
					}
					try {
						consumer.subscribe(SendMsgService.topic, doctype);
						consumer.registerMessageListener(listener);
						consumer.start();
						return true;
					} catch (Exception e) {
						logger.error(String.format("[%s]注册MQ监听失败", listener.getClass().getName()), e);
						return false;
					}
					INSTANCE = tempobj;
				}
			}
		}
		return INSTANCE;
	}
}
