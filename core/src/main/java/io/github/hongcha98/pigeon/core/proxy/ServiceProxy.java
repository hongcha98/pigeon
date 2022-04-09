package io.github.hongcha98.pigeon.core.proxy;

import io.github.hongcha98.pigeon.common.error.PigeonException;
import io.github.hongcha98.pigeon.common.service.Service;
import io.github.hongcha98.pigeon.common.service.ServiceAddress;
import io.github.hongcha98.pigeon.core.RpcMessage;
import io.github.hongcha98.pigeon.found.FoundService;
import io.github.hongcha98.pigeon.loadbalance.LoadBalance;
import io.github.hongcha98.remote.common.Message;
import io.github.hongcha98.remote.common.spi.SpiLoader;
import io.github.hongcha98.remote.core.RemoteClient;

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
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            throw new PigeonException("service provider not found");
        }
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
