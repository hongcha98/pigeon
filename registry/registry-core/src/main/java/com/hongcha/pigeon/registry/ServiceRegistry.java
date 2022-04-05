package com.hongcha.pigeon.registry;

import com.hongcha.pigeon.common.service.metadata.Service;
import com.hongcha.pigeon.common.service.metadata.ServiceAddress;

import java.util.Collection;
import java.util.Set;

public interface ServiceRegistry extends FoundService {
    /**
     * 获取本地服务address
     *
     * @return
     */
    ServiceAddress getLocalServiceiAddress();

    /**
     * 添加注册的service
     *
     * @param service
     */
    void addService(Service service);

    /**
     * 添加集合service
     *
     * @param serviceCollection
     */
    void addAllService(Collection<Service> serviceCollection);

    /**
     * 获取该服务注册的service
     *
     * @return 不可变的list
     */
    Set<Service> getAllService();

    /**
     * 获取注册中心的配置
     *
     * @return
     */
    RegistryConfig getRegistryConfig();

    /**
     * 是否启动
     *
     * @return
     */
    boolean isStart();

    /**
     * 开启服务
     */
    void start();


}
