package net.wicp.tams.commons.mq.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.constant.RabbitExchangeType;

public abstract class SendMsgRabbit {
	public static Result sendExchangeMsg(String exchange, String message) {
		Channel channel = ConnectionObj.getInstance().getChannel();
		try {
			channel.exchangeDeclare(exchange, RabbitExchangeType.fanout.name());
			channel.basicPublish(exchange, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			return Result.getSuc();
		} catch (IOException e) {
			return Result.getError(e.getMessage());
		}
	}

	public static Result sendDicMsg(String queryName, String message) {
		Channel channel = ConnectionObj.getInstance().getChannel();
		try {
			// channel.queueDeclare(queryName, false, false, false, null);
			channel.exchangeDeclare("", RabbitExchangeType.direct.name());
			channel.basicPublish("", queryName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			return Result.getSuc();
		} catch (IOException e) {
			return Result.getError(e.getMessage());
		}
	}
}
