package com.github.xdxiaodao.dynamicip.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/16
 * Time 16:37
 * Desc
 */
public class FileUtils extends org.apache.commons.io.FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 获取property文件内容
     * @param propFile 输入流
     * @return 内容
     */
    public static Properties loadProperties(InputStream propFile) {
        if (null == propFile) {
            return new Properties();
        }

        Properties properties = new Properties();

        try {
            properties.load(propFile);
        } catch (Exception e) {
            logger.error("加载配置文件：{}失败", propFile);
        }

        return properties;
    }
}
