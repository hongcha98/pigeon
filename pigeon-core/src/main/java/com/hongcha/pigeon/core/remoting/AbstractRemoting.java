package com.hongcha.pigeon.core.remoting;

import com.hongcha.pigeon.core.error.PigeonException;
import com.hongcha.pigeon.core.service.metadata.Service;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRemoting {
    protected IDGenerate idGenerate = new PositiveAtomIDGenerate();

    protected final Map<Integer, MessageFuture> messageFutureMap = new ConcurrentHashMap<>();

    protected IDGenerate getIdGenerate() {
        return idGenerate;
    }

    protected Map<Integer, MessageFuture> getMessageFutureMap() {
        return messageFutureMap;
    }

    protected Object sendSync(Channel channel, Service service, String methodName, Object[] params) {
        return this.sendSync(channel, buildRequest(service, methodName, params));
    }

    protected Object sendSync(Channel channel, RpcMessage rpcMessage) {
        MessageFuture messageFuture = this.sendAsync(channel, rpcMessage);
        Object data = messageFuture.getData();
        if (data instanceof Throwable) {
            throw new PigeonException((Throwable) data);
        }
        return data;
    }

    protected MessageFuture sendAsync(Channel channel, Service service, String methodName, Object[] params) {
        return this.sendAsync(channel, buildRequest(service, methodName, params));
    }

    protected MessageFuture sendAsync(Channel channel, RpcMessage rpcMessage) {
        MessageFuture messageFuture = new MessageFuture();
        messageFuture.setRpcRqeuestMessage(rpcMessage);
        messageFutureMap.put(rpcMessage.getId(), messageFuture);
        channel.writeAndFlush(rpcMessage).addListener(f -> {
            if (!f.isSuccess()) {
                messageFutureMap.remove(rpcMessage.getId());
            }
        });
        return messageFuture;
    }


    protected RpcMessage buildRequest(Service service, String methodName, Object[] params) {
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setId(getIDGenerate().next());
        rpcMessage.setService(service);
        rpcMessage.setMethodName(methodName);
        rpcMessage.setParams(params);
        return rpcMessage;
    }

    protected RpcMessage buildRespose(RpcMessage requestMessage, Object body) {
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setId(requestMessage.getId());
        rpcMessage.setService(requestMessage.getService());
        rpcMessage.setMethodName(requestMessage.getMethodName());
        rpcMessage.setParams(requestMessage.getParams());
        rpcMessage.setBody(body);
        return rpcMessage;
    }

    protected void setIdGenerate(IDGenerate idGenerate) {
        this.idGenerate = idGenerate;
    }


    protected IDGenerate getIDGenerate() {
        return this.idGenerate;
    }


}
