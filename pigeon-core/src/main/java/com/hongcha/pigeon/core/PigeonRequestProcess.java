package com.hongcha.pigeon.core;

import com.hongcha.pigeon.core.error.PigeonException;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.remote.common.RequestCommon;
import com.hongcha.remote.common.exception.RemoteExceptionBody;
import com.hongcha.remote.common.process.RequestProcess;
import com.hongcha.remote.core.util.RemoteUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Map;


public class PigeonRequestProcess implements RequestProcess {
    protected final Map<Service, Object> serviceHandlerMap;

    public PigeonRequestProcess(Map<Service, Object> serviceHandlerMap) {
        this.serviceHandlerMap = serviceHandlerMap;
    }

    @Override
    public void process(ChannelHandlerContext ctx, RequestCommon requestCommon) {
        try {
            RpcMessage rpcMessage = RemoteUtils.getBody(requestCommon, RpcMessage.class);
            Service service = rpcMessage.getService();
            Object handler = getHandler(service);
            Object[] params = rpcMessage.getParams();
            Class<?>[] paramTypes = getParamType(rpcMessage.getParamTypes());
            Method invokeMethod = handler.getClass().getDeclaredMethod(rpcMessage.getMethodName(), paramTypes);
            Object resp = invokeMethod.invoke(handler, params);
            rpcMessage.setBody(resp);
            rpcMessage.setParams(null);
            rpcMessage.setParamTypes(null);
            requestCommon.setBody(RemoteUtils.encode(requestCommon.getProtocol(), rpcMessage));
        } catch (Exception e) {
            requestCommon.setCode(500);
            requestCommon.setBody(RemoteUtils.encode(requestCommon.getProtocol(), new RemoteExceptionBody(e)));
        }
        requestCommon.setDirection(true);
        ctx.writeAndFlush(requestCommon);
    }

    @SneakyThrows
    protected Class<?>[] getParamType(String[] params) {
        Class<?>[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            classes[i] = Class.forName(params[i]);
        }
        return classes;
    }

    protected Object getHandler(Service service) {
        Object o = serviceHandlerMap.get(service);
        if (o == null) {
            throw new PigeonException(service.toString() + " no handler");
        }
        return o;
    }

}
