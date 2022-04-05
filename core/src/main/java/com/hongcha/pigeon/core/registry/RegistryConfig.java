package com.hongcha.pigeon.core.registry;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistryConfig {
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

}
