package com.hongcha.pigeon.core.proxy;

import com.hongcha.pigeon.common.service.Service;
import com.hongcha.pigeon.common.service.ServiceAddress;
import com.hongcha.pigeon.core.RpcMessage;
import com.hongcha.pigeon.found.FoundService;
import com.hongcha.pigeon.loadbalance.LoadBalance;
import com.hongcha.remote.common.Message;
import com.hongcha.remote.common.spi.SpiLoader;
import com.hongcha.remote.core.RemoteClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceProxy implements InvocationHandler {
    private final String applicationName;

    private final Service service;

    private final String loadBalanceName;

    private final FoundService foundService;

    private final RemoteClient remotingClient;


    public ServiceProxy(String applicationName, Service service, String loadBalanceName, FoundService foundService, RemoteClient remotingClient) {
        this.applicationName = applicationName;
        this.service = service;
        this.loadBalanceName = loadBalanceName;
        this.foundService = foundService;
        this.remotingClient = remotingClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<ServiceAddress> serviceAddresses = foundService.foundService(applicationName, service);
        LoadBalance loadBalance = SpiLoader.load(LoadBalance.class, loadBalanceName);
        ServiceAddress serviceAddress = (ServiceAddress) loadBalance.choose(applicationName, serviceAddresses);
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
