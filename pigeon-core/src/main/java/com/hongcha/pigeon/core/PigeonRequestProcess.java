package com.hongcha.pigeon.core;

import com.hongcha.pigeon.core.error.PigeonException;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.remoting.common.dto.RequestMessage;
import com.hongcha.remoting.common.process.RequestProcess;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Map;


public class PigeonRequestProcess implements RequestProcess {
    protected final Map<Service, Object> serviceHandlerMap;


    public PigeonRequestProcess(Map<Service, Object> serviceHandlerMap) {
        this.serviceHandlerMap = serviceHandlerMap;
    }

    @SneakyThrows
    @Override
    public RequestMessage proess(RequestMessage requestMessage) {
        RpcMessage rpcMessage = (RpcMessage) requestMessage.getMsg();
        Service service = rpcMessage.getService();
        Object handler = getHandler(service);
        Object[] params = rpcMessage.getParams();
        Class<?>[] paramTypes = getParamType(params);
        Method invokeMethod = handler.getClass().getDeclaredMethod(rpcMessage.getMethodName(), paramTypes);
        Object resp = invokeMethod.invoke(handler, params);
        rpcMessage.setBody(resp);
        return requestMessage;
    }

    protected Class<?>[] getParamType(Object[] params) {
        if (params != null && params.length > 0) {
            Class<?>[] paramType = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                paramType[i] = params[i].getClass();
            }
            return paramType;
        }
        return new Class[0];
    }

    protected Object getHandler(Service service) {
        Object o = serviceHandlerMap.get(service);
        if (o == null) {
            throw new PigeonException(service.toString() + " no handler");
        }
        return o;
    }

}
