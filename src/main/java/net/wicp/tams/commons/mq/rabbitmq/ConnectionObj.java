package net.wicp.tams.commons.mq.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;

public class ConnectionObj implements ShutdownListener {
	private final Logger logger = LogHelp.getLogger(getClass());
	private static Object lockobj = new Object();
	private static volatile ConnectionObj INSTANCE;
	private Connection conn = null;
	private Channel channel = null;

	/***
	 * 双重检查
	 * 
	 * @return
	 */
	public static final ConnectionObj getInstance() {
		if (INSTANCE == null) {
			synchronized (lockobj) {
				if (INSTANCE == null) {
					ConnectionObj tempobj = new ConnectionObj();
					ConnectionFactory factory = new ConnectionFactory();
					factory.setUsername(Conf.get("rabbitmq.server.username"));
					factory.setPassword(Conf.get("rabbitmq.server.password"));
					factory.setVirtualHost(Conf.get("rabbitmq.virtual.host"));
					try {
						// 集群模式,会按照数组顺序获取连接，一旦获取连接成功则就是这样机器的connection
						Map<String, String> hostMap=Conf.getPre("rabbitmq.server.");
						List<Address> hostlist=new ArrayList<>();
						if(MapUtils.isEmpty(hostMap)){
							throw new IllegalArgumentException("rabbitmq没有配置服务器地址");
						}
						Pattern pattern = Pattern.compile("rabbitmq\\.server\\.host.\\.ip"); 
						for (String key : hostMap.keySet()) {
							Matcher matcher = pattern.matcher(key);							
							if(matcher.matches()){
								String ip=hostMap.get(key);
								String port=hostMap.get(key.replace("ip", "port"));
								hostlist.add(new Address(ip,Integer.parseInt(port)));
							}
						}
						if(CollectionUtils.isEmpty(hostlist)){
							throw new IllegalArgumentException("rabbitmq没有配置服务器地址");
						}
						tempobj.conn = factory.newConnection(hostlist.toArray(new Address[hostlist.size()]));
						tempobj.conn.addShutdownListener(tempobj);
						tempobj.channel = tempobj.conn.createChannel();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
					INSTANCE = tempobj;
				}
			}
		}
		return INSTANCE;
	}

	@Override
	public void shutdownCompleted(ShutdownSignalException cause) {
		String exceptionMessage = cause.getMessage();
		logger.info("客户端已关闭：{}", exceptionMessage);
	}

	public Channel getChannel() {
		if (channel == null || !channel.isOpen()) {
			closeChannelAndConnection();
			return getInstance().channel;
		}
		return channel;
	}

	public static void closeChannelAndConnection() {
		if (INSTANCE != null) {
			if (INSTANCE.channel != null && INSTANCE.channel.isOpen()) {
				try {
					INSTANCE.channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
			}
			if (INSTANCE.conn != null && INSTANCE.conn.isOpen()) {
				try {
					INSTANCE.conn.removeShutdownListener(INSTANCE);
					INSTANCE.conn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			INSTANCE = null;
		}
	}

}
