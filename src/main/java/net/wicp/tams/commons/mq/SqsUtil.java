package net.wicp.tams.commons.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.apiext.StringUtil;

/***
 * 亚马逊SQS操作工具
 * 
 * @author andy.zhou
 *
 */
public abstract class SqsUtil {
	private static AmazonSQS sqs = null;
	private static String defaultMqUrl = null;// 默认MQ的地址

	static {
		String accessId = Conf.get("aws_access_key_id");
		String accessPwd = Conf.get("aws_secret_access_key");
		String defaultq = Conf.get("aws_defaultq");
		sqs = new AmazonSQSClient(new BasicAWSCredentials(accessId, accessPwd));
		Region usWest2 = Region.getRegion(Regions.CN_NORTH_1);
		sqs.setRegion(usWest2);
		defaultMqUrl = sqs.getQueueUrl(defaultq).getQueueUrl();// 默认MQ的地址
		// 当属性文件刷新时的热加载
		Conf.addCallBack("SQS", new Conf.Callback() {
			@Override
			public void doReshConf(Properties newProperties) {
				String curKeyIdNew = newProperties.getProperty("aws_access_key_id");
				String curkeyNew = newProperties.getProperty("aws_access_key_id");
				String defaultqNew = newProperties.getProperty("aws_defaultq");
				sqs = new AmazonSQSClient(new BasicAWSCredentials(curKeyIdNew, curkeyNew));
				defaultMqUrl = sqs.getQueueUrl(defaultqNew).getQueueUrl();
			}
		}, "aws_access_key_id", "aws_secret_access_key", "aws_defaultq");
	}

	/****
	 * 发送一条消息到MQ
	 * 
	 * @param mqName
	 *            mq名称
	 * @param msg
	 *            要发送的消息
	 * @return 发送的结果
	 */
	public static Result sendMessage(String mqName, String msg) {
		try {
			String url = StringUtil.isNull(mqName) ? defaultMqUrl : sqs.getQueueUrl(mqName).getQueueUrl();
			SendMessageResult rt = sqs.sendMessage(url, msg);// 一条消息不能超过 8KB
			Result retobj = Result.getSuc();
			retobj.setMessage(rt.getMessageId());
			return retobj;
		} catch (Exception e) {
			return Result.getError(e.getMessage());
		}
	}

	/***
	 * 发送一条消息到默认的MQ
	 * 
	 * @param msg
	 *            要发送的消息
	 * @return 发送的结果
	 */
	public static Result sendMessage(String msg) {
		return sendMessage(null, msg);
	}

	/***
	 * 接收消息
	 * 
	 * @param mqName
	 *            mq名称
	 * @param maxNumber
	 *            最大取消息数量
	 * @param isDel
	 *            取完消息后是否删除 true:删除 false：不删除
	 * @return 接收到的消息列表
	 */
	public static List<String> receiveMessage(String mqName, int maxNumber, boolean isDel) {
		String url = StringUtil.isNull(mqName) ? defaultMqUrl : sqs.getQueueUrl(mqName).getQueueUrl();
		List<Message> msgs = sqs.receiveMessage(new ReceiveMessageRequest(url).withMaxNumberOfMessages(maxNumber))
				.getMessages();
		List<String> retlist = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(msgs)) {
			for (Message msg : msgs) {
				retlist.add(msg.getBody());
				if (isDel) {// 需要删除
					sqs.deleteMessage(new DeleteMessageRequest(url, msg.getReceiptHandle()));
				}
			}
		}
		return retlist;
	}

	/***
	 * 接收默认消息队列的maxNumber条消息
	 * 
	 * @param maxNumber
	 *            最大取消息数量
	 * @param isDel
	 *            取完消息后是否删除 true:删除 false：不删除
	 * @return 接收到的消息列表
	 */
	public static List<String> receiveMessage(int maxNumber, boolean isDel) {
		return receiveMessage(null, maxNumber, isDel);
	}

	/***
	 * 接收默认消息队列的一条消息
	 * 
	 * @param isDel
	 *            取完消息后是否删除 true:删除 false：不删除
	 * @return 得到的消息
	 */
	public static String receiveMessageForOne(boolean isDel) {
		List<String> retlist = receiveMessage(null, 1, isDel);//
		return CollectionUtils.isEmpty(retlist) ? null : retlist.get(0);
	}

}
