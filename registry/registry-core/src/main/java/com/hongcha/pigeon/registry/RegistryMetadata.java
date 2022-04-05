package com.hongcha.pigeon.registry;

import com.hongcha.pigeon.common.service.Service;
import com.hongcha.pigeon.common.service.ServiceAddress;

import java.util.HashSet;
import java.util.Set;

/**
 * 注册元数据
 */
public class RegistryMetadata {
    /**
     * 应用名
     */
    private String applicationName;
    /**
     * 应用服务地址
     */
    private ServiceAddress applicationServiceAddress;

    /**
     * 服务列表
     */
    private Set<Service> serviceList = new HashSet<>();

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }


    public ServiceAddress getServiceAddress() {
        return applicationServiceAddress;
    }

    public void setServiceAddress(ServiceAddress serviceAddress) {
        this.applicationServiceAddress = serviceAddress;
    }

    public Set<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(Set<Service> serviceList) {
        this.serviceList = serviceList;
    }

}
