package com.github.xdxiaodao.dynamicip.util;

import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with dynamicip
 * 自动加载classpath下所有的配置
 * 适用于配置较少的情况
 * User zhangmuzhao
 * Date 2017/6/22
 * Time 16:08
 * Desc
 */
public class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static ConcurrentHashMap<String, Properties> resourceMap = new ConcurrentHashMap<String, Properties>();
    private static Map<String, String> propertiesMap = Maps.newHashMap();

    static {
        URL url = PropertiesUtil.class.getResource("/");
        try {
            Collection<File> files = FileUtils.listFiles(FileUtils.toFile(url), new String[]{"properties"}, true);
            do {
                if (CollectionUtils.isEmpty(files)) {
                    break;
                }

                // 循环处理文件
                for (File file : files) {
                    Properties properties = FileUtils.loadProperties(PropertiesUtil.class.getResourceAsStream("/" + file.getName()));
                    if (null == properties) {
                        continue;
                    }
                    resourceMap.put(file.getName(), properties);
                    if (null != properties && null != properties.keys()) {
                        Enumeration<Object> resourceKey = properties.keys();
                        while (resourceKey.hasMoreElements()) {
                            String key = String.valueOf(resourceKey.nextElement());
                            String value = properties.getProperty(key);
                            if (StringUtils.isNotBlank(key)) {
                                propertiesMap.put(key, properties.getProperty(key));
                            }
                        }
                    }
                }
            } while (1 == 2);
        } catch (Exception e) {
            logger.error("加载配置失败", e);
        }

    }

    /**
     * 根据key获取配置
     * @param key key
     * @return 配置
     */
    public static String getProperties(String key) {
        return propertiesMap.get(key);
    }
}
