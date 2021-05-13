package com.hongcha.pigeon.core.proxy;

import com.hongcha.pigeon.core.registry.FoundService;
import com.hongcha.pigeon.core.remoting.RemotingClient;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    private final Service service;

    private final FoundService foundService;

    private final RemotingClient remotingClient;

    public ServiceProxy(Service service, FoundService foundService, RemotingClient remotingClient) {
        this.service = service;
        this.foundService = foundService;
        this.remotingClient = remotingClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ServiceAddress serviceAddress = foundService.foundService(service);
        return remotingClient.sendSync(serviceAddress, service, method.getName(), args);
    }
}
