package com.github.xdxiaodao.dynamicip.model;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created with dynamicip
 * 动态ip池
 * User zhangmuzhao
 * Date 2017/6/16
 * Time 18:18
 * Desc
 */
public class DynamicIpPool extends PriorityBlockingQueue<DynamicIp> {

    private DynamicIpPool() {

    }

    public static DynamicIpPool me() {
        return new DynamicIpPool();
    }


}
