package com.hongcha.pigeon.core.remoting;

import com.hongcha.pigeon.core.error.PigeonException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MessageFuture {

    private RpcMessage rpcRqeuestMessage;

    private CompletableFuture<Object> messageFuture = new CompletableFuture<>();

    public RpcMessage getRpcRqeuestMessage() {
        return rpcRqeuestMessage;
    }

    public void setRpcRqeuestMessage(RpcMessage rpcRqeuestMessage) {
        this.rpcRqeuestMessage = rpcRqeuestMessage;
    }

    public CompletableFuture<Object> getMessageFuture() {
        return messageFuture;
    }

    public void setMessageFuture(CompletableFuture<Object> messageFuture) {
        this.messageFuture = messageFuture;
    }

    public Object getData() {
        try {
            return messageFuture.get();
        } catch (Exception e) {
            throw new PigeonException(e);
        }
    }

    public Object getData(long timeOut, TimeUnit timeUnit) {
        try {
            return messageFuture.get(timeOut, timeUnit);
        } catch (Exception e) {
            throw new PigeonException(e);
        }
    }
}
