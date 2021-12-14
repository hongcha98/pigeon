package com.hongcha.pigeon.core.proxy;

import com.hongcha.pigeon.core.RpcMessage;
import com.hongcha.pigeon.core.registry.FoundService;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;
import com.hongcha.remoting.common.dto.RequestCommon;
import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.core.RemotingClient;
import com.hongcha.remoting.core.RemotingFactory;

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
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setCode(0);
        requestMessage.setProtocol((byte) 2);
        requestMessage.setDirection(false);

        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setService(service);
        rpcMessage.setMethodName(method.getName());
        rpcMessage.setParams(args);

        requestMessage.setMsg(rpcMessage);

        RequestMessage resp = buildRequestMessage(remotingClient.send(serviceAddress.getIp(), serviceAddress.getPort(), requestMessage).get());

        RpcMessage msg = (RpcMessage) resp.getMsg();

        return msg.getBody();
    }

    protected RequestMessage buildRequestMessage(RequestCommon requestCommon) {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setCode(requestCommon.getCode());
        requestMessage.setProtocol(requestCommon.getProtocol());
        requestMessage.setDirection(requestCommon.getDirection());
        requestMessage.setHeaders(requestCommon.getHeaders());
        Object msg = RemotingFactory.getBody(requestCommon);
        requestMessage.setMsg(msg);
        return requestMessage;
    }


}
