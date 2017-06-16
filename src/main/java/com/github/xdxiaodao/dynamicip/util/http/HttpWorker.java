package com.github.xdxiaodao.dynamicip.util.http;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author nadonghua 负责具体的 http 请求工作,当请求失败后，会根据情况自动重试
 * 代码参考 http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientConfiguration.java
 */
public class HttpWorker {
	private static final Logger log = LoggerFactory.getLogger(HttpWorker.class);
	public static String PRINT_URL = null;

	private static final Map<String, Object> defaultDate = new HashMap<String, Object>();

	static {
		defaultDate.put("statusCode", "500");
		defaultDate.put("entity", "");
		defaultDate.put("result-code", "");
		defaultDate.put("contentType", "");
	}
	/**
	 * 
	 * @param post get post 方式
	 * @param uri url
	 * @param param 参数
	 * @param header header
	 * @param charset 字符
	 * @return key[statusCode,entity,result-code,contentType]
	 */
	public static Map<String, Object> worker(boolean post, String uri,Map<String, String> param, Map<String,String> header,String charset) {
		long start = System.currentTimeMillis();
		try {
			return HttpWorker.http(post, uri, param, header ,charset,3000,75000);
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			log.error("HttpWorker has a exception start:["+ start +"] end:["+ end +"] use:[" + (end -start) + "]");
			log.error("HttpWorker has a exception uri:" + uri + " exception:" + e.getLocalizedMessage() + "  cause:" + e.getCause(),e); 
			return defaultDate;
		}
	}
	
	/**
	 * 
	 * @param post get post 方式
	 * @param uri url
	 * @param param 参数
	 * @param charset 字符
	 * @return key[statusCode,entity,result-code,contentType]
	 */
	public static Map<String, Object> worker(boolean post, String uri,Map<String, String> param, String charset) {
		long start = System.currentTimeMillis();
		try {
			return HttpWorker.http(post, uri, param, null ,charset,3000,75000);
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			log.error("HttpWorker has a exception start:["+ start +"] end:["+ end +"] use:[" + (end -start) + "]");
			log.error("HttpWorker has a exception uri:" + uri + " exception:" + e.getLocalizedMessage() + "  cause:" + e.getCause(),e);
			return defaultDate;
		}
	}

	/**
	 *
	 * @param post get post 方式
	 * @param uri url
	 * @param param 参数
	 * @param charset 字符
	 * @return key[statusCode,entity,result-code,contentType]
	 */
	public static Map<String, Object> worker(String uri,List<Params> param, boolean post, String charset) {
		long start = System.currentTimeMillis();
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			if (param != null) {
				for (Params p : param) {
					if(p != null) {
						if (p.getParmaValue() == null) {
							p.setParmaValue("");
						}
						paramMap.put(p.getParamName(), String.valueOf(p.getParmaValue()));
					}
				}
			}
			return HttpWorker.http(post, uri, paramMap, null, charset, 3000, 75000);
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			log.error("HttpWorker has a exception start:["+ start +"] end:["+ end +"] use:[" + (end -start) + "]");
			log.error("HttpWorker has a exception uri:" + uri + " exception:" + e.getLocalizedMessage() + "  cause:" + e.getCause(),e);
			return defaultDate;
		}
	}

	/**
	 *
	 * @param post get post 方式
	 * @param uri url
	 * @param param 参数
	 * @param charset 字符
	 * @return key[statusCode,entity,result-code,contentType]
	 */
	public static Map<String, Object> worker(String uri,List<Params> param, boolean post, String charset,int connectTimeout,int socketTimeout) {
		long start = System.currentTimeMillis();
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			if (param != null) {
				for (Params p : param) {
					if(p != null) {
						if (p.getParmaValue() == null) {
							p.setParmaValue("");
						}
						paramMap.put(p.getParamName(), String.valueOf(p.getParmaValue()));
					}
				}
			}
			return HttpWorker.http(post, uri, paramMap, null, charset, connectTimeout, socketTimeout);
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			log.error("HttpWorker has a exception start:["+ start +"] end:["+ end +"] use:[" + (end -start) + "]");
			log.error("HttpWorker has a exception uri:" + uri + " exception:" + e.getLocalizedMessage() + "  cause:" + e.getCause(),e);
			return defaultDate;
		}
	}

	/**
	 * 
	 * @param post
	 * @param uri
	 * @param param
	 * @param charset
	 * @param connectTimeout 链接超时时间
	 * @param socketTimeout  读取超时时间
	 * @return key[statusCode,entity,result-code,contentType]
	 */
	public static Map<String, Object> worker(boolean post, String uri,Map<String, String> param, String charset,int connectTimeout,int socketTimeout) {
		long start = System.currentTimeMillis();
		try {
			return HttpWorker.http(post, uri, param, null, charset,connectTimeout,socketTimeout);
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			log.error("HttpWorker has a exception start:["+ start +"] end:["+ end +"] use:[" + (end -start) + "]");
			log.error("HttpWorker has a exception uri:" + uri + " exception:" + e.getLocalizedMessage() + "  cause:" + e.getCause(),e);
			return defaultDate;
		}
	}
	
	private static Map<String, Object> http(boolean post, String uri,Map<String, String> param, Map<String, String> headerMap,String charset,int connectTimeout,int socketTimeout) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.putAll(defaultDate);

		CloseableHttpClient httpClient = (CloseableHttpClient) HttpClientFactory.generateCilent();

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		if(param != null) {
			for (Entry<String, String> entry : param.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				BasicNameValuePair basic = new BasicNameValuePair(key, value);
				paramList.add(basic);
			}
		}
		
		if(socketTimeout <=0 || socketTimeout > 120000){
			socketTimeout = 30000;
		}
		if(connectTimeout <=0 || connectTimeout > 12000){
			connectTimeout = 3000;
		}

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(socketTimeout)	//读取超时
				.setConnectTimeout(connectTimeout)	//连接超时
				.build();							//设置请求和传输超时时间

		CloseableHttpResponse httpResponse = null;
		Header[] headers = null;
		if(headerMap!=null && headerMap.size() > 0){
			headers = new Header[headerMap.size()];
			int i =0;
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				headers[i] = new BasicHeader(entry.getKey(), entry.getValue());
				i++;
			}
		} else {
			headers = new Header[0];
		}

		//判断是否零时输出请求参数，为方便查看日志提供
		if(StringUtils.isNotBlank(PRINT_URL)){
			log.info("HttpWorker http url = "+uri);
			log.info("HttpWorker http param = "+param);
			PRINT_URL = null;
		}

		//uri不能为空
		if(StringUtils.isBlank(uri)){
			log.warn("HttpWorker.http error uri is empty");
			return data;
		}
		try {
			if (post) {
				HttpPost httpPost = new HttpPost(uri.trim());
				httpPost.setEntity(new UrlEncodedFormEntity(paramList, charset));
				httpPost.setConfig(requestConfig);
				httpPost.setHeaders(headers);
				httpResponse = (CloseableHttpResponse) httpClient.execute(httpPost);
			} else {
				if (paramList != null && uri.indexOf("?") < 0) {
					uri = uri.trim() + "?";
				}
				String url = uri + URLEncodedUtils.format(paramList, charset);
				HttpGet httpGet = new HttpGet(url);
				httpGet.setConfig(requestConfig);
				httpGet.setHeaders(headers);
				httpResponse = (CloseableHttpResponse) httpClient.execute(httpGet);
			}
			
			String content = null;
			String responseCode = null;
			String contentType = null;
			int statusCodeInt = 0;
			
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				content = EntityUtils.toString(entity);
				EntityUtils.consume(entity);
			}
			
			statusCodeInt = httpResponse.getStatusLine().getStatusCode();
			Header head = httpResponse.getFirstHeader("result-code");
			if (head != null) {
				responseCode = head.getValue();
			}
			Header headCntType = httpResponse.getLastHeader("Content-Type");
			if (headCntType != null){
				contentType = headCntType.getValue();
			}
			//以下返回内容为统一返回值，keyName不可修改
			data.put("statusCode", statusCodeInt+"");
			data.put("entity", content);
			data.put("result-code", responseCode);
			data.put("contentType", contentType);
			data.put("header", httpResponse.getAllHeaders());
		} catch (Exception e){
			log.error("HttpWorker.http error",e);
		}finally {
			try {
				httpResponse.close();
			}catch (Exception e){
				log.warn("http response close error");
			}
		}
		return data;
	}

	/**
	 * 获取http请求中的内容部分
	 * @param post 是否post请求
	 * @param url 请求地址Map<K,V>
	 * @param param 请求参数
	 * @param charset 字符集
	 * @return
	 */
	public static String getHttpContent(boolean post,String url,Map<String, String> param,String charset){
		Map<String, Object> objectMap = worker(post, url, param, charset);

		Object obj;
		if(objectMap != null && (obj = objectMap.get("entity")) != null){
			return obj.toString();
		}
		return null;
	}

	/**
	 * 获取http请求中的内容部分
	 * @param post 是否post请求
	 * @param url 请求地址List<Params>
	 * @param param 请求参数
	 * @param charset 字符集
	 * @return
	 */
	public static String getHttpContent(String url,List<Params> param,boolean post,String charset){
		Map<String, Object> objectMap = worker( url, param, post, charset);

		Object obj;
		if(objectMap != null && (obj = objectMap.get("entity")) != null){
			return obj.toString();
		}
		return null;
	}

	public static void main(String[] args){
		Map<String, Object> worker = worker(false, "http://m.dhgate.com/search.do?key=test&vt=0", null, "");
		System.out.println(worker);

		worker.put("name1", null);
		if(worker.get("name1") != null){
			System.out.println(worker.get("name1").toString());
		}

	}
}
