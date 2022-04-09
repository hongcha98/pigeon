package io.github.hongcha98.pigeon.found;

import io.github.hongcha98.pigeon.common.service.Service;
import io.github.hongcha98.pigeon.common.service.ServiceAddress;

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
