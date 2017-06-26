package com.github.xdxiaodao.dynamicip.service.strategy;

import com.github.xdxiaodao.dynamicip.model.DynamicIp;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/22
 * Time 16:03
 * Desc
 */
public class ProxyFailedStrategy implements IpUnEffectiveStrategy{
    @Override
    public boolean isEffective(DynamicIp dynamicIp) {
        if (dynamicIp.getFailedCount() > 5) {
            return false;
        }

        return true;
    }
}
