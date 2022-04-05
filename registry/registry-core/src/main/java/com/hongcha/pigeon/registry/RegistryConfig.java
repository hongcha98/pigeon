package com.hongcha.pigeon.registry;

import java.util.HashMap;
import java.util.Map;

public class RegistryConfig {
    private String type = "zookeeper";

    /**
     * 连接地址 ip:port ,号分割
     */
    private String address;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 其他属性
     */
    private Map<String, String> property = new HashMap<>();

    public RegistryConfig() {
    }

    public Map<String, String> getProperty() {
        return property;
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    public RegistryConfig(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
