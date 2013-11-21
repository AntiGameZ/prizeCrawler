package com.ruyicai.prizecrawler.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

import flexjson.JSONDeserializer;

/**
 * HTTP工具箱
 * 
 * 
 */
public final class HttpTookit {

	private static Logger logger = LoggerFactory.getLogger(HttpTookit.class);

	private static final String LOTTERY = ResourceBundle.getBundle("url")
			.getString("lottery.url");

	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	public static String doGet(String url, String queryString) {
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(6000);
		client.getParams().setParameter(HttpMethodParams.USER_AGENT, SystemCode.UA_IE9);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 15000);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		try {
			if (StringUtils.isNotBlank(queryString))
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			}
		} catch (URIException e) {
			logger.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
		} catch (IOException e) {
			logger.error("执行HTTP Get请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	public static String doPost(String url, Map<String, String> params) {
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new PostMethod(url);
		client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 150000);

		// 设置Http Post数据
		if (!params.isEmpty()) {
			NameValuePair[] data = new NameValuePair[params.size()];
			int i = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				data[i] = new NameValuePair(entry.getKey(),entry.getValue());
				i = i + 1;
			}
			method.setQueryString(data);
		}

		try {
			int statuscode = client.executeMethod(method);
//			System.out.println(method.getStatusCode());
			if (statuscode == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			}
		} catch (Exception e) {
			response = null;
			logger.error("执行HTTP Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}

		return response;
	}
	
	
	public static String getResponse(String url, String method, String encoding, Map<String, String> params, int timeout) {
		OutputStream out = null;
		InputStream in = null;
		
		StringBuilder body = new StringBuilder();
		for(String key:params.keySet()) {
			body.append(key).append("=").append(params.get(key)).append("&");
		}
		
		if(body.toString().endsWith("&")) {
			body = body.deleteCharAt(body.toString().length()-1);
		}
		try {
			URL postUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) postUrl
					.openConnection();
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			if(!"GET".equals(method)) {
				conn.setDoOutput(true);
			}
			conn.setDoInput(true);
			conn.setRequestMethod(method);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			conn.connect();
			if(!"GET".equals(method)) {
				if(!StringUtil.isEmpty(body.toString())) {
					out = conn.getOutputStream();
					out.write(body.toString().getBytes());
					out.flush();
				}
			}
			in = conn.getInputStream();
			String result = read(in, encoding);
			return result;
		} catch (MalformedURLException e) {
			logger.info("请求地址有误, url:" + url, e);
			throw new RuntimeException("请求地址有误, url:" + url);
		} catch (Exception e) {
			logger.info("IO错误,url:" + url + ",body:" + body, e);
			throw new RuntimeException("请求地址有误, url:" + url);
		} finally {
			close(in);
			close(out);
		}
	}
	
	public static String read(InputStream in, String encoding) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while(null != (line = reader.readLine())) {
				builder.append(line);
			}
			return builder.toString();
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			close(in);
		}
	}
	
	private static void close(InputStream in) {
		if(null != in) {
			try {
				in.close();
			} catch(Exception e) {
			} 
		}
	}
	
	private static void close(OutputStream out) {
		if(null != out) {
			try {
				out.close();
			} catch(Exception e) {
				
			}
		}
	}

	public static String POST(String url, Map<String, String> params,
			Map<String, String> requestProperties,String charsetName) throws IOException {
		logger.info("请求shishicai.cn:url="+url);
		URL shi = new URL(url);
		HttpURLConnection uc = (HttpURLConnection) shi.openConnection();
		uc.setReadTimeout(4000);
		uc.setConnectTimeout(10000);
		uc.setDoOutput(true);
		uc.setDoInput(true);
		uc.setRequestMethod("POST");
		uc.setUseCaches(false);
		uc.setInstanceFollowRedirects(true);
		uc.setRequestProperty("User-Agent", SystemCode.UA_IE9);
		if ((null!=requestProperties)&&(!requestProperties.isEmpty())) {
			for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
				uc.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		uc.connect();

		StringBuilder req = new StringBuilder();
		if (!params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				req.append(entry.getKey()).append("=").append(entry.getValue())
						.append("&");
			}
			if (req.toString().endsWith("&")) {
				req = req.deleteCharAt(req.length() - 1);
			}
		}

		OutputStream out = uc.getOutputStream();
		out.write(req.toString().getBytes());
		BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream(), charsetName), 1024 * 1024);
		String line;
		String sTotalString = "";
		while ((line = in.readLine()) != null) {
			sTotalString += line;
		}
//		System.out.println(sTotalString);
		return sTotalString;

	}

	public static boolean notice(PrizeInfo prizeInfo) {
		logger.info("notice start:lotno=" + prizeInfo.getLotno() + " batchcode="
				+ prizeInfo.getBatchcode() + " winbasecode"
				+ prizeInfo.getWinbasecode() + " winspecialcode"
				+ prizeInfo.getWinspecialcode());
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("lotno", prizeInfo.getLotno());
			params.put("batchcode", prizeInfo.getBatchcode());
			params.put("winbasecode", prizeInfo.getWinbasecode());
			params.put("winspecialcode", prizeInfo.getWinspecialcode());

			logger.info("notice doPost:"+LOTTERY + "/system/draw");
			String rep = doPost(LOTTERY + "/system/draw", params);
			logger.info("notice 返回"+rep);
			
			JSONDeserializer<HashMap<String,Object>> json = new JSONDeserializer<HashMap<String,Object>>();
			Map<String,Object> map = json.deserialize(rep);
			if (map != null && "0".equals((String) map.get("errorCode"))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.info("notice 失败",e);
			return false;
		}
	}
	
	
	
	
	public static boolean notice2(PrizeInfo prizeInfo) {
		logger.info("notice start:lotno=" + prizeInfo.getLotno() + " batchcode="
				+ prizeInfo.getBatchcode() + " winbasecode"
				+ prizeInfo.getWinbasecode() + " winspecialcode"
				+ prizeInfo.getWinspecialcode());
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("lotno", prizeInfo.getLotno());
			params.put("batchcode", prizeInfo.getBatchcode());
			params.put("winbasecode", prizeInfo.getWinbasecode());
			params.put("winspecialcode", prizeInfo.getWinspecialcode());

			logger.info("notice doPost:"+"http://192.168.150.120/lottery" + "/system/draw");
			String rep = doPost("http://192.168.150.120/lottery" + "/system/draw", params);
			logger.info("notice 返回"+rep);
			
			JSONDeserializer<HashMap<String,Object>> json = new JSONDeserializer<HashMap<String,Object>>();
			Map<String,Object> map = json.deserialize(rep);
			if (map != null && "0".equals((String) map.get("errorCode"))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.info("notice 失败",e);
			return false;
		}
	}
	
	
	
	/**
	 * 通知开奖号码
	 * @param lotno
	 * @param batchcode
	 * @param winbasecode
	 * @param winspecialcode
	 * @return
	 */
	public static boolean noticeCode(String lotno,String batchcode,String winbasecode,String winspecialcode,String variable) {

		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("lotno", lotno);
			params.put("batchcode", batchcode);
			params.put("winbasecode", winbasecode);
			params.put("winspecialcode", winspecialcode);
			params.put("variable", variable);

			logger.info("notice doPost:"+LOTTERY + "/system/draw");
//			String rep = doPost(LOTTERY + "/system/draw", params);
			
			String rep = getResponse(LOTTERY + "/system/draw", "POST", "UTF-8", params, 20000);
			logger.info("notice 返回"+rep);
			
			JSONDeserializer<HashMap<String,Object>> json = new JSONDeserializer<HashMap<String,Object>>();
			Map<String,Object> map = json.deserialize(rep);
			if (map != null && "0".equals((String) map.get("errorCode"))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.info("notice 失败",e);
			return false;
		}
	}
	
	
	/**
	 * 通知开奖详细信息
	 * @param lotno
	 * @param batchcode
	 * @param info
	 * @return
	 */
	public static boolean noticeInfo(String lotno,String batchcode,String info) {

		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("lotno", lotno);
			params.put("batchcode", batchcode);
			params.put("info", info);

			logger.info("notice doPost:"+LOTTERY + "/system/saveinfo");
			String rep = doPost(LOTTERY + "/system/saveinfo", params);
			logger.info("notice 返回"+rep);
			
			JSONDeserializer<HashMap<String,Object>> json = new JSONDeserializer<HashMap<String,Object>>();
			Map<String,Object> map = json.deserialize(rep);
			if (map != null && "0".equals((String) map.get("errorCode"))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.info("notice 失败",e);
			return false;
		}
	}
	
	
	public static String getWebSiteCookie(String url) {
		String cookie = "";
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(4000);
			GetMethod get = new GetMethod(url);  
			get.setFollowRedirects(false);
			get.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 6000);
			client.executeMethod(get);  
			cookie = Arrays.toString(client.getState().getCookies());
			cookie = cookie.substring(1,cookie.length()-1);
			cookie = cookie.split(",")[0];
		}catch(Exception e) {
			logger.info("获取cookie失败");
			cookie = "";
		}
		return cookie;
		
		
	}

}
