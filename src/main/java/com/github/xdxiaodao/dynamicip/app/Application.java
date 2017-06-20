package com.github.xdxiaodao.dynamicip.app;

import com.github.xdxiaodao.dynamicip.model.DynamicIp;
import com.github.xdxiaodao.dynamicip.service.DynamicIpService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with bookspider
 * User zhangmz
 * Date 2017/5/23
 * Time 18:51
 * Desc
 */
public class Application {

    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext(
                new String[]{"classpath*:applicationContext.xml"});

        DynamicIpService bookSpiderService = context.getBean(DynamicIpService.class);

        while (true) {
            try {
                Thread.sleep(1000);
                DynamicIp dynamicIp = bookSpiderService.getEffectiveIp();
                if (null != dynamicIp) {
                    System.out.println("get dynamic ip:" + dynamicIp.getIp() + ", port:" + dynamicIp.getPort());
                } else {
                    System.out.println("ip is null");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
