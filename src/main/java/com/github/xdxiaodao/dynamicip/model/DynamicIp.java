package com.github.xdxiaodao.dynamicip.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with dynamicip
 * 动态IP
 * User zhangmuzhao
 * Date 2017/5/31
 * Time 18:44
 * Desc
 */
public class DynamicIp implements Comparable<DynamicIp>{
    private String ip;
    private Integer port;
    private String country;
    private String province;
    private String city;
    private String isp;
    private String type;
    private String anonymity; // 是否匿名
    private Integer replyMsTime; // 响应时间，以毫秒为单位
    private AtomicInteger useCount = new AtomicInteger(0); // 使用次数
    private AtomicInteger failedCount = new AtomicInteger(0); // 失败次数
    private AtomicBoolean isEffective = new AtomicBoolean(true); // 是否有效


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnonymity() {
        return anonymity;
    }

    public void setAnonymity(String anonymity) {
        this.anonymity = anonymity;
    }

    public Integer getReplyMsTime() {
        return replyMsTime;
    }

    public void setReplyMsTime(Integer replyMsTime) {
        this.replyMsTime = replyMsTime;
    }

    public Integer getUseCount() {
        return useCount.get();
    }

    public void setUseCount(Integer useCount) {
        this.useCount.set(useCount);
    }

    public void incCount() {
        this.useCount.getAndIncrement();
    }

    public boolean getIsEffective() {
        return isEffective.get();
    }

    public void setIsEffective(boolean isEffective) {
        this.isEffective.getAndSet(isEffective);
    }

    public void noEffective(boolean isEffective) {
        this.isEffective.getAndSet(isEffective);
    }

    public Integer getFailedCount() {
        return failedCount.get();
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount.getAndSet(failedCount);
    }

    public void incFailedCount() {
        int tmpFailedCount = this.failedCount.incrementAndGet();
    }

    @Override
    public int compareTo(DynamicIp o) {
        if (null == o) {
            return 1;
        }
        int diff = Integer.valueOf(this.useCount.get()).compareTo(o.getUseCount());
        if (diff != 0) {
            return diff;
        }

        return o.getIp().compareTo(this.getIp());
    }

    public Pair<String, Integer> getKey() {
        return Pair.of(ip, port);
    }
}
