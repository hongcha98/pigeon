package com.hongcha.pigeon.core.proxy;

import com.hongcha.pigeon.core.registry.FoundService;
import com.hongcha.pigeon.core.remoting.RemotingClinet;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    private final Service service;

    private final FoundService foundService;

    private final RemotingClinet remotingClinet;

    public ServiceProxy(Service service, FoundService foundService, RemotingClinet remotingClinet) {
        this.service = service;
        this.foundService = foundService;
        this.remotingClinet = remotingClinet;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ServiceAddress serviceAddress = foundService.foundService(service);
        return remotingClinet.sendSync(serviceAddress, service, method.getName(), args);
    }
}
