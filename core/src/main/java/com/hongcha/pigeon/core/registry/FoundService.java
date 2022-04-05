package com.hongcha.pigeon.core.registry;

import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;

public interface FoundService {
    /**
     * 查看服务提供商的address
     *
     * @param service
     * @return
     */
    ServiceAddress foundService(Service service);
}
