package com.github.xdxiaodao.dynamicip.service.spider;

import com.github.xdxiaodao.dynamicip.model.DynamicIp;
import com.github.xdxiaodao.dynamicip.model.DynamicIpPool;
import com.github.xdxiaodao.dynamicip.util.FileUtils;
import com.github.xdxiaodao.dynamicip.util.IpUtils;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import sun.net.util.IPAddressUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/16
 * Time 16:10
 * Desc
 */
public class Data5uPageProcessor implements PageProcessor {
    private DynamicIpPool dynamicIpPool;
    private static final String DATA5U_IP_TRANS_FILE = "/data5u-ip-trans.properties";
    private static Properties portMap;
    private AtomicBoolean isProcess = new AtomicBoolean(false); // 是否在处理中

    static {
        portMap = FileUtils.loadProperties(Data5uPageProcessor.class.getResourceAsStream(DATA5U_IP_TRANS_FILE));
    }

    public Data5uPageProcessor(DynamicIpPool dynamicIps) {
        this.dynamicIpPool = dynamicIps;
    }

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page page
     */
    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Selectable selectableList = html.xpath("//div[@class=wlist]/li/ul[@class=l2]");
        if (CollectionUtils.isEmpty(selectableList.nodes())) {
            return;
        }

        List<Selectable> ipNodes = selectableList.nodes();
        for (Selectable tmpIp : ipNodes) {
            Selectable ipNode = tmpIp.xpath("//ul/span/li");
            if (CollectionUtils.isEmpty(ipNode.nodes())) {
                continue;
            }

            List<Selectable> ipInfoNodes = ipNode.nodes();
            String ip = ipInfoNodes.get(0).xpath("//li/text()").toString();
            String portStr = ipInfoNodes.get(1).xpath("//li/text()").toString();
            String portClassStr = ipInfoNodes.get(1).$("li", "class").toString();
            String portClass = portClassStr.replace("port", "").trim();

            String isAnonymity = ipInfoNodes.get(2).xpath("//li/a/text()").toString();
            String protocalType = ipInfoNodes.get(3).xpath("//li/a/text()").toString();
            String country = ipInfoNodes.get(4).xpath("//li/a/text()").toString();
            String city = ipInfoNodes.get(5).xpath("//li/a/text()").toString();
            String lsp = ipInfoNodes.get(6).xpath("//li/a/text()").toString();
            String time = ipInfoNodes.get(7).xpath("//li/text()").toString();

            Integer port = 0;
            if (NumberUtils.isDigits(portStr)) {
                port = NumberUtils.stringToInt(portStr);
            }

            DynamicIp dynamicIp = new DynamicIp();
            dynamicIp.setIp(ip);
            dynamicIp.setPort(portMap.containsKey(portClass) ? NumberUtils.toInt(portMap.getProperty(portClass)) : port);
            dynamicIp.setAnonymity(isAnonymity);
            dynamicIp.setCountry(country);
            dynamicIp.setCity(city);
            dynamicIp.setIsp(lsp);
            dynamicIp.setReplyMsTime(NumberUtils.stringToInt(time.replace("秒", "")));

            if (IpUtils.isHostConnectable(ip, dynamicIp.getPort())) {
                this.dynamicIpPool.put(dynamicIp);
            }
        }

        this.setIsProcess(false);
    }

    /**
     * get the site settings
     *
     * @return site
     * @see us.codecraft.webmagic.Site
     */
    @Override
    public Site getSite() {
        Site site = Site.me().setRetryTimes(3).setSleepTime(100).setUserAgent("");
        site.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        site.setUseGzip(true).setCharset("UTF-8");
        String cookie = "auth=6656e49839ec998a02a5a86106abe88c; UM_distinctid=15cc00af8518de-090a35201e7e4b-323f5c0f-15f900-15cc00af852e39; JSESSIONID=3188427A8C944989559BFB198E49E03D; Hm_lvt_3406180e5d656c4789c6c08b08bf68c2=1497870563,1497924244,1497928589; Hm_lpvt_3406180e5d656c4789c6c08b08bf68c2=1497940282; CNZZDATA1260383977=36023414-1497870563-%7C1497937549";
        String[] cookieArr = StringUtils.split(cookie, "; ");
        if (ArrayUtils.isNotEmpty(cookieArr)) {
            for (String tmpCookie : cookieArr) {
                String key = StringUtils.substringBefore(tmpCookie, "=");
                String value = StringUtils.substringAfter(tmpCookie, "=");
                site.addCookie(key, value);
            }
        }
        return site;
    }

    public boolean getIsProcess() {
        return isProcess.get();
    }

    public void setIsProcess(boolean isProcess) {
        this.isProcess.getAndSet(isProcess);
    }

    public static void main(String[] args) {
        Spider.create(new Data5uPageProcessor(DynamicIpPool.me())).addUrl("http://www.data5u.com/free/gnpt/index.shtml").thread(1).run();
    }
}
