package com.hongcha.pigeon.core.proxy;

import com.hongcha.pigeon.common.service.metadata.Service;
import com.hongcha.pigeon.common.service.metadata.ServiceAddress;
import com.hongcha.pigeon.core.RpcMessage;
import com.hongcha.pigeon.registry.FoundService;
import com.hongcha.remote.common.Message;
import com.hongcha.remote.core.RemoteClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceProxy implements InvocationHandler {
    private final Service service;

    private final FoundService foundService;

    private final RemoteClient remotingClient;

    public ServiceProxy(Service service, FoundService foundService, RemoteClient remotingClient) {
        this.service = service;
        this.foundService = foundService;
        this.remotingClient = remotingClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ServiceAddress serviceAddress = foundService.foundService(service);
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setService(service);
        rpcMessage.setMethodName(method.getName());
        rpcMessage.setParams(args);
        rpcMessage.setParamTypes(buildParamTypes(method));
        Message message = remotingClient.buildRequest(rpcMessage, 0);
        RpcMessage respMessage = remotingClient.send(serviceAddress.getIp(), serviceAddress.getPort(), message, RpcMessage.class);
        return respMessage.getBody();
    }

    private String[] buildParamTypes(Method method) {
        return Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList()).toArray(new String[0]);
    }

}
