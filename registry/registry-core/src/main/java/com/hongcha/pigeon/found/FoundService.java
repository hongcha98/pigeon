package com.hongcha.pigeon.found;

import com.hongcha.pigeon.common.service.Service;
import com.hongcha.pigeon.common.service.ServiceAddress;

import java.util.List;

public interface FoundService {
    /**
     * 查看服务提供商的address
     *
     * @param applicationName 应用名
     * @param service         服务信息
     * @return
     */
    List<ServiceAddress> foundService(String applicationName, Service service);
}
