package com.github.xdxiaodao.dynamicip.service.strategy;

import com.github.xdxiaodao.dynamicip.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/22
 * Time 16:04
 * Desc
 */
public class StrategyFactory {

    private static final String STRATEGY_KEY = "ip.uneffective.strategy.name";

    private static IpUnEffectiveStrategy unEffectiveStrategy;

    /**
     * 获取配置的使用策略
     * @return 策略
     * @throws Exception 异常
     */
    public static IpUnEffectiveStrategy getStrategy() throws Exception {
        if (null != unEffectiveStrategy) {
            return unEffectiveStrategy;
        }

        String strategy = PropertiesUtil.getProperties(STRATEGY_KEY);
        if (StringUtils.isBlank(strategy)) {
            throw new Exception("not config the ip.uneffective.strategy.name");
        }

        Class clazz = Class.forName(StrategyFactory.class.getPackage().getName() + "." + strategy + "Strategy");
        if (null == clazz) {
            throw new Exception("strategy:" + strategy + " 不存在");
        }

        unEffectiveStrategy = (IpUnEffectiveStrategy) clazz.newInstance();
        return unEffectiveStrategy;
    }
}
