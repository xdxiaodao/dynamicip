package com.github.xdxiaodao.dynamicip.service;

import com.github.xdxiaodao.dynamicip.model.DynamicIp;
import com.github.xdxiaodao.dynamicip.model.DynamicIpPool;
import com.github.xdxiaodao.dynamicip.service.spider.Data5uPageProcessor;
import com.github.xdxiaodao.dynamicip.util.FileUtils;
import com.github.xdxiaodao.dynamicip.util.http.HttpWorker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/5/31
 * Time 18:37
 * Desc
 */
@Service
public class DynamicIpService implements InitializingBean{
    private static Logger logger = LoggerFactory.getLogger(DynamicIpService.class);
    private static final String DYNAMIC_IP_CONFIG_FILE = "dynamic-ip.properties";
    private static final Integer DEFAULT_CYCLE_SPIDER_TIME = 5 * 60 * 1000;

    private ExecutorService spiderExecutor = Executors.newSingleThreadExecutor();

    private String spiderUrl;

    private Integer cycleSpiderTime;

    private DynamicIpPool dynamicIpPool = DynamicIpPool.me();

    @PostConstruct
    public void init() {
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getResourceAsStream(DYNAMIC_IP_CONFIG_FILE);
            Properties properties = FileUtils.loadProperties(inputStream);
            spiderUrl = properties.getProperty("ip.spider.host");
            if (StringUtils.isEmpty(spiderUrl)) {
                spiderUrl = "http://www.data5u.com/free/gngn/index.shtml";
            }

            cycleSpiderTime = NumberUtils.toInt(properties.getProperty("cycle.spider.time"), DEFAULT_CYCLE_SPIDER_TIME);
        } catch (Exception e) {
            logger.error("获取spider url失败", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        spiderExecutor.submit(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(cycleSpiderTime);
                        Spider.create(new Data5uPageProcessor(dynamicIpPool)).addUrl(spiderUrl).thread(1).run();
                    } catch (Exception e) {
                        logger.error("爬取ip信息失败", e);
                    }
                }
            }
        });
    }

    public DynamicIp getEffectiveIp(String url) {
        DynamicIp dynamicIp = dynamicIpPool.peek();
        while (!isEffectiveIp(dynamicIp, url)) {
            dynamicIpPool.remove();
            dynamicIp = dynamicIpPool.peek();
        }

        dynamicIp.incCount();
        return dynamicIp;
    }

    private boolean isEffectiveIp(DynamicIp dynamicIp, String url) {
        return false;
    }
}
