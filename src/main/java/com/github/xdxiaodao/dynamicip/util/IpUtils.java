package com.github.xdxiaodao.dynamicip.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created with dynamicip
 * User zhangmuzhao
 * Date 2017/6/19
 * Time 10:18
 * Desc
 */
public class IpUtils {
    private static Logger logger = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 设备是否可达
     * @param host host名称
     * @param port 端口
     * @return 是否可用
     */
    public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(1000);
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            logger.error("{}:{} 链接错误", host, port);
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("{}:{} 关闭链接失败", host, port);
            }
        }
        return true;
    }
}
