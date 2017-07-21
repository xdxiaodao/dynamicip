package com.github.xdxiaodao.dynamicip.service;

import com.github.xdxiaodao.dynamicip.model.DynamicIp;
import com.github.xdxiaodao.dynamicip.model.DynamicIpPool;
import com.github.xdxiaodao.dynamicip.service.strategy.IpUnEffectiveStrategy;
import com.github.xdxiaodao.dynamicip.service.strategy.StrategyFactory;
import com.github.xdxiaodao.dynamicip.service.spider.Data5uPageProcessor;
import com.github.xdxiaodao.dynamicip.util.FileUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
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
    private static final Integer DEFAULT_IP_POOL_MONITOR_CHECK_NUM = 5000;

    private ExecutorService spiderExecutor = Executors.newSingleThreadExecutor();

    private ScheduledExecutorService ipPoolMonitor = Executors.newScheduledThreadPool(1, new BasicThreadFactory.Builder().namingPattern("ipPoolThread").build());

    private String spiderUrl;

    private Integer cycleSpiderTime;

    private Integer retryCount;

    private Integer ipPoolMinNum;

    private Integer ipPoolMonitorCheckNum;

    private DynamicIpPool dynamicIpPool = DynamicIpPool.me();

    private Data5uPageProcessor data5uPageProcessor;

    @PostConstruct
    public void init() {
        // 添加本机IP
        DynamicIp dynamicIp = new DynamicIp();
        dynamicIp.setIp("localhost");
        dynamicIpPool.add(dynamicIp);

        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getResourceAsStream(DYNAMIC_IP_CONFIG_FILE);
            Properties properties = FileUtils.loadProperties(inputStream);
            spiderUrl = properties.getProperty("ip.spider.host");
            if (StringUtils.isEmpty(spiderUrl)) {
                spiderUrl = "http://www.data5u.com/free/gngn/index.shtml";
            }

//            cycleSpiderTime = NumberUtils.toInt(properties.getProperty("cycle.spider.time"), DEFAULT_CYCLE_SPIDER_TIME);
            retryCount = NumberUtils.toInt(properties.getProperty("get.effective.ip.retry.count"), DEFAULT_CYCLE_SPIDER_TIME);
            ipPoolMinNum = NumberUtils.toInt(properties.getProperty("ip.pool.min.num"), 2);
            ipPoolMonitorCheckNum = NumberUtils.toInt(properties.getProperty("ip.pool.monitor.check.time"), DEFAULT_IP_POOL_MONITOR_CHECK_NUM);
        } catch (Exception e) {
            logger.error("获取spider 配置失败", e);
        }

        data5uPageProcessor = new Data5uPageProcessor(dynamicIpPool);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ipPoolMonitor.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (dynamicIpPool.size() < ipPoolMinNum) {
                            logger.info("ip pool num is low, execute data5u page processor");
                            startSpider();
                        }

                        Thread.sleep(ipPoolMonitorCheckNum);
                    } catch (Exception e) {
                        logger.error("monitor ip pool error!", e);
                    }
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    private void asyncSpider() {
        spiderExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("execute data5u page processor");
                    startSpider();
                } catch (Exception e) {
                    logger.error("爬取ip信息失败", e);
                }
            }
        });
    }

    private synchronized void startSpider() {
        boolean isRunning = data5uPageProcessor.getIsProcess();
        if (!isRunning) {
            logger.info("启动新进程进行爬取");
            data5uPageProcessor.setIsProcess(true);
            List<SpiderListener> spiderListenerList = Lists.newArrayList();
            spiderListenerList.add(new DynamicIpListener(data5uPageProcessor));
            Spider.create(data5uPageProcessor).setSpiderListeners(spiderListenerList).addUrl(spiderUrl).thread(1).run();
        } else {
            logger.info("当前ip爬取进程正在运行，不启动新进程");
        }
    }

    /**
     * 获取ip池中所有ip
     * @return 所有ip
     */
    public List<DynamicIp> getAllIp() {
        if (dynamicIpPool.size() > 0) {
            return Arrays.asList(dynamicIpPool.toArray(new DynamicIp[]{}));
        }

        return Lists.newArrayList();
    }

    /**
     * 获取单一有效IP
     * @return 有效ip
     */
    public DynamicIp getEffectiveIp() {
        DynamicIp dynamicIp = null;
        try {
            // 空池判断
            if (dynamicIpPool.size() == 0) {
                asyncSpider();
                return null;
            }

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
//        if (!IpUtils.isHostConnectable(dynamicIp.getIp(), dynamicIp.getPort())) {
//            return false;
//        }

        // 判断是否有效
        return dynamicIp.getIsEffective();
    }

    /**
     * 设置ip无效
     * @param dynamicIp
     */
    public void setUnEffective(DynamicIp dynamicIp) {
        dynamicIp.incFailedCount();
        // 根据策略判断是否有效
        try {
            IpUnEffectiveStrategy strategy = StrategyFactory.getStrategy();
            dynamicIp.setIsEffective(strategy.isEffective(dynamicIp));
        } catch (Exception e) {
            logger.error("获取ip失效策略失败", e);
        }
    }

    public void setEffective(DynamicIp dynamicIp) {
        dynamicIp.setIsEffective(true);
    }


    public static class DynamicIpListener implements SpiderListener {
        private Data5uPageProcessor pageProcessor;

        public DynamicIpListener(Data5uPageProcessor pageProcessor) {
            this.pageProcessor = pageProcessor;
        }

        @Override
        public void onSuccess(Request request) {
            pageProcessor.setIsProcess(false);
        }

        @Override
        public void onError(Request request) {
            pageProcessor.setIsProcess(false);
        }
    }
}
