package com.github.xdxiaodao.dynamicip.model;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/21
 * Time 17:36
 * Desc
 */
public class IpSpiderHost {

    private String mainHost;

    private String failoverHost;

    public String getMainHost() {
        return mainHost;
    }

    public void setMainHost(String mainHost) {
        this.mainHost = mainHost;
    }

    public String getFailoverHost() {
        return failoverHost;
    }

    public void setFailoverHost(String failoverHost) {
        this.failoverHost = failoverHost;
    }
}
