package com.github.xdxiaodao.dynamicip.service.strategy;

import com.github.xdxiaodao.dynamicip.model.DynamicIp;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/22
 * Time 16:03
 * Desc
 */
public interface IpUnEffectiveStrategy {

    public boolean isEffective(DynamicIp dynamicIp);
}
