package com.hongcha.pigeon.common.service;

import java.util.Objects;

public class ServiceAddress {
    /**
     * ip
     */
    private String ip;
    /**
     * port
     */
    private int port;

    /**
     * 不推荐使用，主要是为了反序列化，set方法同理
     */
    public ServiceAddress() {

    }

    public ServiceAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }


    public int getPort() {
        return port;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceAddress that = (ServiceAddress) o;
        return port == that.port && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public static ServiceAddress parse(String address) {
        String[] split = address.split(":");
        return new ServiceAddress(split[0], Integer.valueOf(split[1]));
    }
}
