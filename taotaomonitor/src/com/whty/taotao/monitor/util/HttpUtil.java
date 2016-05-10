package com.whty.taotao.monitor.util;
 

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Http请求帮助类
 * 
 */
public class HttpUtil {

	private static int TIMEOUT = 50000;//50秒
	
	private static String getRequestUrl(String url, List<NameValuePair> parameters, String charset) {

		StringBuilder sb = new StringBuilder(url);
		int index = url.indexOf("?");
		if (index >= 0) {
			if (index < url.length() - 1) {
				index = url.lastIndexOf("&");
				if (index < url.length() - 1) {
					sb.append("&");
				}
			}
		} else {
			sb.append("?");
		}

		if (parameters != null) {
			if (charset == null || "".equals(charset.trim())) {
				charset = "utf-8";
			}
			for (NameValuePair pair : parameters) {
				try {
					url += URLEncoder.encode(pair.getName(), charset) + "=" + URLEncoder.encode(pair.getValue(), charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sb.append("&");
			}
		}

		return sb.substring(0, sb.length() - 1);
	}

	public static String get(String url, String responseEncoding) throws Exception {

		String rst = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();

			conn.setUseCaches(false);
			conn.setReadTimeout(TIMEOUT);
			conn.setRequestMethod("GET");

			conn.connect();
			is = conn.getInputStream();
			byte[] bytes = IOUtils.toByteArray(is);
			rst = new String(bytes, responseEncoding);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.disconnect();
			} catch (Exception e) {
			}
			try {
				is.close();
			} catch (Exception e2) {
			}
		}
		return rst;
	}

	public static String get(String url, List<NameValuePair> parameters, String requestEncoding, String responseEncoding) throws Exception {
		String u = getRequestUrl(url, parameters, requestEncoding);
		return get(u, responseEncoding);
	}

	public static String post(String url, String contentType, String content, Map<String, String> headers, String requestEncoding, String responseEncoding) throws Exception {

		String rst = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setReadTimeout(TIMEOUT);
			conn.setRequestMethod("POST");

			conn.setRequestProperty("Content-type", contentType);

			if (headers != null) {
				for (String k : headers.keySet()) {
					conn.setRequestProperty(k, headers.get(k));
				}
			}

			conn.connect();
			os = conn.getOutputStream();
			byte[] b = content.getBytes(requestEncoding);
			os.write(b, 0, b.length);
			os.flush();
			os.close();

			is = conn.getInputStream();
			byte[] bytes = IOUtils.toByteArray(is);
			rst = new String(bytes, responseEncoding);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.disconnect();
			} catch (Exception e) {
			}
			try {
				is.close();
			} catch (Exception e2) {
			}
			try {
				os.close();
			} catch (Exception e2) {
			}
		}
		return rst;
	}

	public static String post(String url, String contentType, List<NameValuePair> parameters, Map<String, String> headers, String requestEncoding, String responseEncoding) throws Exception {

		if (StringUtils.isEmpty(requestEncoding)) {
			requestEncoding = "utf-8";
		}

		StringBuilder sb = new StringBuilder(url);

		for (NameValuePair pair : parameters) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(pair.getName());
			sb.append("=");
			sb.append(pair.getValue());
		}

		return post(url, contentType, sb.toString(), headers, requestEncoding, responseEncoding);
	}
	public static void main(String[] args) {
		String dataXML = "";
			try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream("c:\\TEMP\\taotaoorder.txt"), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String s;
			while ((s = bReader.readLine()) != null) {
				sb.append(s.trim());
			}
			dataXML = sb.toString();
			bReader.close();
			System.out.println(dataXML);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long start = System.currentTimeMillis();
//		String result = HttpUtil.post("http://127.0.0.1:8080/ModernTaoPlugIn/newcoupon/getConponForGoodsList", "application/json; charset=UTF-8", dataXML, null, "utf-8", "utf-8");
//		String result = HttpUtil.post("http://218.206.30.144:18080/ModernTaoPlugIn/newcoupon/getConponForGoodsList", "application/json; charset=UTF-8", dataXML, null, "utf-8", "utf-8");
		String result;
		try {
			result = HttpUtil.post("http://218.206.30.144:18080/ModernTaoPlugIn/order/create", "application/json; charset=UTF-8", dataXML, null, "utf-8", "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String result = HttpUtil.post("http://127.0.0.1:8080/ModernTaoPlugIn/order/create", "application/json; charset=UTF-8", dataXML, null, "utf-8", "utf-8");
//		String result = HttpUtil.post("http://127.0.0.1:8080/ModernTaoPlugIn/newcoupon/receiveNewCoupon", "application/json;charset=UTF-8", dataXML, null, "utf-8", "utf-8");
//		String result = HttpUtil.post("http://218.206.30.144:18080/ModernTaoPlugIn/newcoupon/receiveNewCoupon", "application/json; charset=UTF-8", dataXML, null, "utf-8", "utf-8");
		
//		String result = HttpUtil.get("http://218.206.30.144:18080/ModernTaoPlugIn/newcoupon/receiveNewCoupon?userid=0d4f677512de4a3584871ff77d67923c&couponcode=gKMyR7FJ", null, "utf-8", "utf-8");
		System.out.println((System.currentTimeMillis()-start)/1000);
		
	}
	
}
