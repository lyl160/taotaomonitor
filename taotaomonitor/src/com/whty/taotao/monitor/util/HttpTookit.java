package com.whty.taotao.monitor.util;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * HTTP工具箱
 * 
 * @author
 */
public final class HttpTookit {
	private static Log log = LogFactory.getLog(HttpTookit.class);
	private static int TimeoutInMilliseconds = 5000;

	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 * @throws IOException 
	 */
	public static String doGet(String url, String queryString, String charset,
			boolean pretty) throws IOException {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(TimeoutInMilliseconds); 
		try {
			if (StringUtils.isNotBlank(queryString))
				// 对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (URIException e) {
			log.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
			throw e;
		} catch (IOException e) {
			log.error("执行HTTP Get请求" + url + "时，发生异常！", e);
			throw e;
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 */
	public static String doPost(HttpClient client,String url, NameValuePair[] data,
			String charset, boolean pretty) {
		StringBuffer response = new StringBuffer();
		if (client==null) {
			client = new HttpClient();
		}
		client.getHttpConnectionManager().getParams().setConnectionTimeout(TimeoutInMilliseconds); 
		PostMethod method = new PostMethod(url);
		method.setRequestBody(data);
		// HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
		try {
			// 执行postMethod
			int statusCode = client.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			log.error("执行HTTP Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	public static NameValuePair[] getUrl(String index) {
		String dataXML = "";
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(new FileInputStream("./xmlData/"
							+ index + ".xml"), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String s;
			while ((s = bReader.readLine()) != null) {
				sb.append(s.trim());
			}
			dataXML = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		NameValuePair[] data = { new NameValuePair("type", index),
				new NameValuePair("dataXML", dataXML) };
		return data;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		NameValuePair[] data = { 
				new NameValuePair("sfzh", "41300119840807601X"),
				new NameValuePair("password", "NzI0OTAz"),
//				new NameValuePair("storecode", "4006082033003901"),
//				new NameValuePair("ordertime", "20150615111111"),
//				new NameValuePair("receivecellphone", "15902726311"),
//				new NameValuePair("extstate", "5"),
//				new NameValuePair("ordersum", "13.50"),
//				new NameValuePair("detail", URLEncoder.encode("备注","UTF-8")),
//				new NameValuePair("sign", "40cf3cfe223dbc1d4749e204b6f75c12"),
				 };
//		String y = doPost("http://localhost:8080/ModernTaoPlugOut/order/createVirtual",data, "UTF-8", true);
		HttpClient client = new HttpClient();
			
			String y = doPost(client,"http://218.201.89.115:8003/ggfw/LoginBLH_login.do",data, "UTF-8", true);
			System.out.println(y);
			String y1 = doPost(client,"http://218.201.89.115:8003/ggfw/QueryBLH_main.do?code=000",data, "UTF-8", true);
			System.out.println(y1);
		
		
		
		
//		String y = doGet("http://218.206.30.144:18080/ModernTaoPlugIn/store/pvuv?userId=1234&storeId=153&path=www.baidu.com", null, "UTF-8", true);
//		System.out.println(y);
//		
		
//		String url ="http://www.china-wee.com/news/9-66.aspx";
//		String y = doGet(url, null, "UTF-8", true);
//		System.out.println(y);
		
		
		
		
//		NameValuePair[] data = { 
//				new NameValuePair("VbrandId", "15"),
//				 };
//		String y = doPost("http://218.206.24.71:8082/ModernTaoPlugIn/goods/search",data, "UTF-8", true);
//		System.out.println(y);
	}
}