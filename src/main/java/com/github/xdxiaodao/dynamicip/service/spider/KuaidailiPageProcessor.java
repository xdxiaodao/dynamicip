package com.github.xdxiaodao.dynamicip.service.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/23
 * Time 14:59
 * Desc
 */
public class KuaidailiPageProcessor implements PageProcessor{
    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page page
     */
    @Override
    public void process(Page page) {

    }

    /**
     * get the site settings
     *
     * @return site
     * @see us.codecraft.webmagic.Site
     */
    @Override
    public Site getSite() {
        Site site = Site.me().setRetryTimes(3).setSleepTime(100);
        return site;
    }
}
