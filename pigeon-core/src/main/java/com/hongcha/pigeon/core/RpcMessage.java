package com.hongcha.pigeon.core;

import com.hongcha.pigeon.core.service.metadata.Service;

import java.io.Serializable;

public class RpcMessage implements Serializable {

    /**
     * service提供商
     */
    private Service service;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 方法请求参数
     */
    private Object[] params;

    /**
     * 方法参数类型
     */
    private String[] paramTypes;


    private Object body;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(String[] paramTypes) {
        this.paramTypes = paramTypes;
    }
}