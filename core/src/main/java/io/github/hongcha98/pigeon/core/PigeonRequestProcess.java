package io.github.hongcha98.pigeon.core;

import io.github.hongcha98.pigeon.common.service.Service;
import io.github.hongcha98.pigeon.common.service.handler.ServiceHandlerFactory;
import io.github.hongcha98.remote.common.Message;
import io.github.hongcha98.remote.common.exception.RemoteExceptionBody;
import io.github.hongcha98.remote.common.process.Process;
import io.github.hongcha98.remote.core.util.ProtocolUtils;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;


public class PigeonRequestProcess implements Process {
    protected final ServiceHandlerFactory serviceHandlerFactory;

    public PigeonRequestProcess(ServiceHandlerFactory serviceHandlerFactory) {
        this.serviceHandlerFactory = serviceHandlerFactory;
    }

    @Override
    public void process(ChannelHandlerContext ctx, Message message) {
        try {
            RpcMessage rpcMessage = ProtocolUtils.decode(message, RpcMessage.class);
            Service service = rpcMessage.getService();
            Object handler = serviceHandlerFactory.getHandler(service);
            Object[] params = rpcMessage.getParams();
            Class<?>[] paramTypes = getParamType(rpcMessage.getParamTypes());
            Method invokeMethod = handler.getClass().getDeclaredMethod(rpcMessage.getMethodName(), paramTypes);
            Object resp = invokeMethod.invoke(handler, params);
            rpcMessage.setBody(resp);
            rpcMessage.setParams(null);
            rpcMessage.setParamTypes(null);
            message.setBody(ProtocolUtils.encode(message, rpcMessage));
        } catch (Exception e) {
            message.setCode(500);
            message.setBody(ProtocolUtils.encode(message, new RemoteExceptionBody(e)));
        }
        message.setDirection(true);
        ctx.writeAndFlush(message);
    }


    protected Class<?>[] getParamType(String[] params) throws ClassNotFoundException {
        Class<?>[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            classes[i] = Class.forName(params[i]);
        }
        return classes;
    }


}
