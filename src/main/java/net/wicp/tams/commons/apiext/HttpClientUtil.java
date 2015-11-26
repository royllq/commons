package net.wicp.tams.commons.apiext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

/***
 * http辅助使用类
 * 
 * @author andy.zhou
 *
 */
public abstract class HttpClientUtil {
	private final static Logger logger = LogHelp.getLogger(HttpClientUtil.class);

	/**
	 * 发送Get请求
	 * 
	 * @param url
	 *            发送请求地址
	 * @return 返回内容
	 * 
	 * @throws ProjectException
	 *             请求异常
	 */
	public static String sendGet(String url, String ecode) throws ProjectException {
		String result = null;
		HttpClientBuilder httpClient = HttpClientBuilder.create();
		HttpGet get = new HttpGet(url);
		InputStream in = null;
		try {
			HttpResponse response = httpClient.build().execute(get);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity = new BufferedHttpEntity(entity);
				in = entity.getContent();
				byte[] read = new byte[1024];
				byte[] all = new byte[0];
				int num;
				while ((num = in.read(read)) > 0) {
					byte[] temp = new byte[all.length + num];
					System.arraycopy(all, 0, temp, 0, all.length);
					System.arraycopy(read, 0, temp, all.length, num);
					all = temp;
				}
				result = new String(all, ecode);
			}
		} catch (Exception e) {
			logger.error("客户端连接错误。");
			throw new ProjectException(ExceptAll.Project_default);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					logger.error("关闭流错误。");
					throw new ProjectException(ExceptAll.project_streamclose);
				}
			get.abort();
		}
		return result;
	}

	public static String sendGet(String url) throws ProjectException {
		return sendGet(url, Conf.get("common.encode"));
	}

	/**
	 * 发送带参数的Get请求
	 * 
	 * @param url
	 *            发送请求地址
	 * @param params
	 *            发送请求的参数
	 * @return 返回内容
	 * @throws ProjectException
	 *             请求异常
	 */
	public static String sendGet(String url, Map<String, String> params) throws ProjectException {
		Set<String> keys = params.keySet();
		StringBuilder urlBuilder = new StringBuilder(url + "?");
		for (String key : keys) {
			urlBuilder.append(key).append("=").append(params.get(key)).append("&");
		}
		urlBuilder.delete(urlBuilder.length() - 1, urlBuilder.length());
		return sendGet(urlBuilder.toString());
	}

	/**
	 * 发送带参数的post请求
	 * 
	 * @param url
	 *            发送请求地址
	 * @param params
	 *            发送请求的参数
	 * @return 返回内容
	 * @throws ProjectException
	 *             请求异常
	 */
	public static String sendPost(String url, Map<String, String> params) throws ProjectException {
		String result = null;
		HttpClientBuilder httpClient = HttpClientBuilder.create();
		HttpPost get = new HttpPost(url);
		// 创建表单参数列表
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		Set<String> keys = params.keySet();
		for (String key : keys) {
			qparams.add(new BasicNameValuePair(key, params.get(key)));
		}
		try {
			// 填充表单
			get.setEntity(new UrlEncodedFormEntity(qparams, Conf.get("common.encode")));

			HttpResponse response = httpClient.build().execute(get);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity = new BufferedHttpEntity(entity);

				InputStream in = entity.getContent();
				byte[] read = new byte[1024];
				byte[] all = new byte[0];
				int num;
				while ((num = in.read(read)) > 0) {
					byte[] temp = new byte[all.length + num];
					System.arraycopy(all, 0, temp, 0, all.length);
					System.arraycopy(read, 0, temp, all.length, num);
					all = temp;
				}
				result = new String(all, "UTF-8");
				if (null != in) {
					in.close();
				}
			}
			get.abort();

			return result;
		} catch (Exception e) {
			throw new ProjectException(ExceptAll.Project_default);
		}

	}

}
