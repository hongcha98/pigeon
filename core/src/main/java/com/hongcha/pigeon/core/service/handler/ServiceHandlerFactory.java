package com.hongcha.pigeon.core.service.handler;

import com.hongcha.pigeon.core.service.metadata.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务handler工厂
 */
public class ServiceHandlerFactory {
    private Map<Service, Object> serviceHandlerMap = new HashMap<>();

    public void register(Service service, Object handler) {
        serviceHandlerMap.put(service, handler);
    }

    public Object getHandler(Service service) {
        Object handler = serviceHandlerMap.get(service);
        if (handler == null) {
            throw new NullPointerException(service.toString() + " no handler ");
        }
        return handler;
    }

    public Map<Service, Object> getAll() {
        return Collections.unmodifiableMap(serviceHandlerMap);
    }

}
