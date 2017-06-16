package com.github.xdxiaodao.dynamicip.util.http;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 
 * @author nadonghua
 * httpcilent http 请求重试handler
 * 
 * look 
 * http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d4e292
 */
public class CustomHttpRequestRetryHandler implements HttpRequestRetryHandler {
	private static final Logger log = LoggerFactory.getLogger(CustomHttpRequestRetryHandler.class);
	
	@Override
	public boolean retryRequest(IOException exception, int executionCount,HttpContext context) {
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        log.error("[HttpRequestRetryHandler] auto retry times:" + executionCount  + " " + request.toString());
        //重试三次
        if (executionCount > 3) {
            return false;
        }
        if (exception instanceof Exception) {
            return true;
        }
        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
        if (idempotent) {
            return true;
        }
        return false;
	}

}
