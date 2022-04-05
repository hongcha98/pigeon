package com.hongcha.pigeon.registry;

import com.hongcha.pigeon.found.FoundService;

public interface ServiceRegistry extends FoundService {
    /**
     * 获取注册元数据
     *
     * @return
     */
    RegistryMetadata getRegistryMetadata();

    /**
     * 获取注册中心的配置
     *
     * @return
     */
    RegistryConfig getRegistryConfig();

    /**
     * 开启服务
     */
    void start();


}
