package net.wicp.tams.commons.mq.rocketmq;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;

public class AcceptMsgRocket {
	private final static Logger logger = LogHelp.getLogger(AcceptMsgRocket.class);

	/****
	 * 一般的注册监听事件
	 * 
	 * @param group
	 *            组
	 * @param listener
	 *            监听对象
	 * @param topic
	 *            监听主题
	 * @param tag
	 *            监听tag
	 * @return true:注册成功 false:注册失败
	 */
	public static boolean RegistListener(String group, MessageListenerConcurrently listener, String topic, String tag) {
		if (StringUtils.isBlank(group)) {
			return false;
		}
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
		/**
		 * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
		 * 如果非第一次启动，那么按照上次消费的位置继续消费
		 */
		consumer.setConsumerGroup(group);
		consumer.setNamesrvAddr(Conf.get("rocketmq.server.namesrvaddr"));
		try {
			consumer.setInstanceName("MSG_ACCEPT-" + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e1) {
			consumer.setInstanceName("inst");
		}
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		try {
			consumer.subscribe(StringUtils.isBlank(topic) ? Conf.get("rocketmq.server.defaulttopic") : topic,
					StringUtils.isBlank(tag) ? Conf.get("rocketmq.server.defaulttag") : tag);
			consumer.registerMessageListener(listener);
			consumer.start();
			return true;
		} catch (Exception e) {
			logger.error(String.format("[%s]注册MQ监听失败", listener.getClass().getName()), e);
			return false;
		}
	}

	/***
	 * 监听配置中指定的tag
	 * 
	 * @param group
	 *            组
	 * @param listener
	 *            监听对象
	 * @param topic
	 *            监听主题
	 * @return true:注册成功 false:注册失败
	 */
	public boolean RegistListener(String group, MessageListenerConcurrently listener, String topic) {
		return RegistListener(group, listener, topic, null);
	}

	/***
	 * 监听配置中指定的topic和指定的tag
	 * 
	 * @param group
	 *            组
	 * @param listener
	 *            监听对象
	 * @return true:注册成功 false:注册失败
	 */
	public boolean RegistListener(String group, MessageListenerConcurrently listener) {
		return RegistListener(group, listener, null, null);
	}

	/***
	 * 监听指定主题的所有tag
	 * 
	 * @param group
	 *            组
	 * @param listener
	 *            监听对象
	 * @param topic
	 *            监听主题
	 * @return true:注册成功 false:注册失败
	 */
	public boolean RegistListenerAll(String group, MessageListenerConcurrently listener, String topic) {
		return RegistListener(group, listener, topic, "*");
	}

	/***
	 * 监听配置中的默认主题的所有tag
	 * 
	 * @param group
	 *            组
	 * @param listener
	 *            监听对象
	 * @return true:注册成功 false:注册失败
	 */
	public boolean RegistListenerAll(String group, MessageListenerConcurrently listener) {
		return RegistListener(group, listener, null, "*");
	}

}
