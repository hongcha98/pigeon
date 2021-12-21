package com.hongcha.pigeon.core.proxy;

import com.hongcha.pigeon.core.RpcMessage;
import com.hongcha.pigeon.core.registry.FoundService;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;
import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.core.RemoteClient;
import com.hongcha.remote.core.util.RemoteUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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
        RequestCommon request = new RequestCommon();
        request.setCode(0);
        request.setProtocol((byte) 2);
        request.setDirection(false);

        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setService(service);
        rpcMessage.setMethodName(method.getName());
        rpcMessage.setParams(args);
        rpcMessage.setParamTypes(buildParamTypes(method));
        request.setBody(RemoteUtils.encode(request.getProtocol(), rpcMessage));

        CompletableFuture<RequestCommon> future = remotingClient.send(serviceAddress.getIp(), serviceAddress.getPort(), request);

        RequestCommon resp = future.get(30, TimeUnit.SECONDS);

        Class<?> returnType = method.getReturnType();

        if (returnType == Void.class) {
            return null;
        }

        return RemoteUtils.getBody(resp, RpcMessage.class).getBody();
    }

    private String[] buildParamTypes(Method method) {
        return Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList()).toArray(new String[0]);
    }

}
