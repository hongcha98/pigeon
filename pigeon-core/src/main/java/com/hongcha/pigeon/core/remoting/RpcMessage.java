package com.hongcha.pigeon.core.remoting;


import com.hongcha.pigeon.core.service.metadata.Service;

import java.io.Serializable;

public class RpcMessage implements Serializable {
    /**
     * 消息id
     */
    private int id;
    /**
     * service提供商
     */
    private Service service;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 方法参数
     */
    private Object[] params;

    /**
     * 返回体
     */
    private Object body;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

}
