package com.github.xdxiaodao.dynamicip.util.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

/**
 * 
 * @author nadonghua 基于 apache http client 4.3.5 包，工厂模式 自定义 customize httpClient 端 工厂
 * 
 *         代码参考
 *         http://hc.apache.org/httpcomponents-client-ga/httpclient/examples
 *         /org/apache/http/examples/client/ClientConfiguration.java
 */
public class HttpClientFactory {
	private static final Logger log = LoggerFactory.getLogger(HttpClientFactory.class);
	private static HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory();
	private static SSLContext sslcontext = SSLContexts.createSystemDefault();
	private static Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
			.register("http", PlainConnectionSocketFactory.INSTANCE)
			.register("https", new SSLConnectionSocketFactory(sslcontext))
			.build();

	private static PoolingHttpClientConnectionManager poolConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connFactory);

	private static RequestConfig defaultRequestConfig = RequestConfig
			.custom()
			.setExpectContinueEnabled(true)
			.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
			.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
			.build();
	// 自动重试策略
	private static HttpRequestRetryHandler customHttpRequestRetryHandler = new CustomHttpRequestRetryHandler();
	
	
	static{
		SocketConfig socketConfig = SocketConfig.custom()
	            .setTcpNoDelay(true)
	            .build();
		poolConnectionManager.setDefaultSocketConfig(socketConfig);

        // Create connection configuration
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .build();
        // Configure the connection manager to use connection configuration either
        // by default or for a specific host.
        poolConnectionManager.setDefaultConnectionConfig(connectionConfig);

        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        poolConnectionManager.setMaxTotal(200);
        poolConnectionManager.setDefaultMaxPerRoute(20);
	}

	public static HttpClient generateCilent() {
		log.debug("get  http client  ");
		CloseableHttpClient httpclient = HttpClients.custom()
				.setConnectionManager(poolConnectionManager)
				.setDefaultRequestConfig(defaultRequestConfig)
				.setRetryHandler(customHttpRequestRetryHandler)
				.build();
		return httpclient;
	}
	
}
