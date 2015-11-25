package net.wicp.tams.commons.test.other;

import java.util.Observable;
import java.util.Observer;

import org.junit.Assert;
import org.junit.Test;

import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.mq.rabbitmq.AcceptMsgRabbit;
import net.wicp.tams.commons.mq.rabbitmq.SendMsgRabbit;

public class TestRabbitMq {
	@Test
	public void testSendDicMsg() {
		Result result = SendMsgRabbit.sendDicMsg("pay.queue", "hello world");
		Assert.assertEquals(true, result.isSuc());
	}

	@Test
	public void testSendFanoutMsg() {
		Result result = SendMsgRabbit.sendFanoutMsg("hello world2");
		Assert.assertEquals(true, result.isSuc());
	}

	@Test
	public void testAccept() {
		AcceptMsgRabbit.getInstance().addObserver(new Observer() {			
			@Override
			public void update(Observable o, Object arg) {
				System.out.println(arg);
			}
		});
	}
}
