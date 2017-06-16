package com.github.xdxiaodao.dynamicip.service.spider;

import com.github.xdxiaodao.dynamicip.model.DynamicIp;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/16
 * Time 16:10
 * Desc
 */
public class Data5uPageProcessor implements PageProcessor {
    public Data5uPageProcessor(PriorityBlockingQueue<DynamicIp> dynamicIps) {

    }

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page page
     */
    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Selectable chapterSelectableList = html.xpath("//div[@class=chapter]/a");
    }

    /**
     * get the site settings
     *
     * @return site
     * @see us.codecraft.webmagic.Site
     */
    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(3).setSleepTime(100);
    }
}
