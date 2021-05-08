package com.hongcha.pigeon.core.service.handler;

import com.hongcha.pigeon.core.service.metadata.Service;

import java.util.Objects;

public class ServiceHandler {
    /**
     * 服务提供商
     */
    private Service service;
    /**
     * 服务处理
     */
    private Object handler;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceHandler that = (ServiceHandler) o;
        return Objects.equals(service, that.service) && Objects.equals(handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, handler);
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Object getHandler() {
        return handler;
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }
}
