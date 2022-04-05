package com.hongcha.pigeon.registry;

import com.hongcha.pigeon.common.service.metadata.Service;
import com.hongcha.pigeon.common.service.metadata.ServiceAddress;

public interface FoundService {
    /**
     * 查看服务提供商的address
     *
     * @param service
     * @return
     */
    ServiceAddress foundService(Service service);
}
