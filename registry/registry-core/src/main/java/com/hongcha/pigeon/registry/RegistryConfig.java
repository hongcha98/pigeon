package com.hongcha.pigeon.registry;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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

    public RegistryConfig(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }
}
