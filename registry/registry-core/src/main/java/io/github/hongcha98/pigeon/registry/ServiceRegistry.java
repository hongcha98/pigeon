package io.github.hongcha98.pigeon.registry;

import io.github.hongcha98.pigeon.found.FoundService;

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

    /**
     * 关闭服务
     */
    void close();

}
