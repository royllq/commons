package net.wicp.tams.commons.mq.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.constant.RabbitExchangeType;

public abstract class SendMsgRabbit {
	/***
	 * 通过fanout类型转发器来发送消息
	 * 
	 * @param exchange
	 *            转发器，一定是fanout类型的
	 * @param message
	 *            消息
	 * @return
	 */
	public static Result sendFanoutMsg(String exchange, String message) {
		if (StringUtil.isNull(exchange) || StringUtil.isNull(message)) {
			return Result.getError("exchange或消费缺失");
		}
		Channel channel = ConnectionObj.getInstance().getChannel();
		try {
			//channel.exchangeDeclare(exchange, RabbitExchangeType.fanout.name(), true);
			channel.basicPublish(exchange, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			return Result.getSuc();
		} catch (IOException e) {
			return Result.getError(e.getCause().toString());
		}
	}

	public static Result sendFanoutMsg(String message) {
		return sendFanoutMsg(Conf.get("rabbitmq.server.default.exchangename"), message);
	}

	/***
	 * 发送消息到指定的Query
	 * 
	 * @param queryName
	 *            query名称
	 * @param message
	 *            消息
	 * @return
	 */
	public static Result sendDicMsg(String queryName, String message) {
		Channel channel = ConnectionObj.getInstance().getChannel();
		try {
			//channel.exchangeDeclare(RabbitExchangeType.direct.getDefaultExchange(), RabbitExchangeType.direct.name());
			channel.basicPublish("", queryName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			return Result.getSuc();
		} catch (IOException e) {
			return Result.getError(e.getCause().toString());
		}
	}

	/****
	 * 直接发送消息到默认的Query
	 * 
	 * @param message
	 *            消息
	 * @return 返回的结果
	 */
	public static Result sendDicMsg(String message) {
		return sendDicMsg(AcceptMsgRabbit.DefaultTQuery, message);
	}
}
