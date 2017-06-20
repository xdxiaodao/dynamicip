package com.github.xdxiaodao.dynamicip.service;

import com.github.xdxiaodao.dynamicip.model.DynamicIp;
import com.github.xdxiaodao.dynamicip.model.DynamicIpPool;
import com.github.xdxiaodao.dynamicip.service.spider.Data5uPageProcessor;
import com.github.xdxiaodao.dynamicip.util.FileUtils;
import com.github.xdxiaodao.dynamicip.util.IpUtils;
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
import java.util.concurrent.*;

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
    private static final String DYNAMIC_IP_CONFIG_FILE = "/dynamic-ip.properties";
    private static final Integer DEFAULT_CYCLE_SPIDER_TIME = 5 * 60 * 1000;
    private static final Integer DEFAULT_RETRY_COUNT = 10;

    private ExecutorService spiderExecutor = Executors.newSingleThreadExecutor();

    private String spiderUrl;

    private Integer cycleSpiderTime;

    private Integer retryCount;

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
            retryCount = NumberUtils.toInt(properties.getProperty("get.effective.ip.retry.count"), DEFAULT_CYCLE_SPIDER_TIME);
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
                        logger.info("execute data5u page processor");
                        Spider.create(new Data5uPageProcessor(dynamicIpPool)).addUrl(spiderUrl).thread(1).run();
                        Thread.sleep(cycleSpiderTime);
                    } catch (Exception e) {
                        logger.error("爬取ip信息失败", e);
                    }
                }
            }
        });
    }

    public DynamicIp getEffectiveIp() {
        DynamicIp dynamicIp = null;
        try {
            // @todo 优化建议
            // 1.缺少空池，或者池中ip少的判断，
            // 2.规则单一，如某个ip使用时间较长，无法指定切换到某个ip
            dynamicIp = dynamicIpPool.poll(500, TimeUnit.MILLISECONDS);
            int failedCount = 0;
            while (!isEffectiveIp(dynamicIp)) {
                if (failedCount > DEFAULT_CYCLE_SPIDER_TIME) {
                    logger.info("Get dynamic ip error!");
                    return null;
                }

                if (null != dynamicIp) {
                    dynamicIp.setIsEffective(false);
//                    dynamicIpPool.offer(dynamicIp, 500, TimeUnit.MILLISECONDS);
                }

                failedCount++;
                dynamicIp = dynamicIpPool.poll(500, TimeUnit.MILLISECONDS);
            }

            dynamicIp.incCount();
            dynamicIpPool.offer(dynamicIp, 500, TimeUnit.MILLISECONDS);
            return dynamicIp;
        } catch (InterruptedException e) {
//            e.printStackTrace();
            logger.error("获取队列元素失败", e);
            return null;
        }
    }

    private boolean isEffectiveIp(DynamicIp dynamicIp) {
        if (null == dynamicIp) {
            return false;
        }
        if (!IpUtils.isHostConnectable(dynamicIp.getIp(), dynamicIp.getPort())) {
            return false;
        }

        // 判断是否有效
        return dynamicIp.getIsEffective();
    }
}
