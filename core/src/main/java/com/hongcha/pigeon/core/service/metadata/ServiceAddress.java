package com.hongcha.pigeon.core.service.metadata;

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
}
