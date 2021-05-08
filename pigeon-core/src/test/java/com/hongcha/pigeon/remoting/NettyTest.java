package com.hongcha.pigeon.remoting;

import com.hongcha.pigeon.core.remoting.RemotingClinet;
import com.hongcha.pigeon.core.remoting.RemotingServer;
import com.hongcha.pigeon.core.remoting.RpcMessage;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;
import org.junit.Before;
import org.junit.Test;

public class NettyTest {
    @Before
    public void startServer() {
        RemotingServer remotingServer = new RemotingServer(9090, null);
        remotingServer.start();
    }


    @Test
    public void startClient() throws InterruptedException {
        RemotingClinet remotingClinet = new RemotingClinet();
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setId(1);
        Object o = remotingClinet.sendSync(new ServiceAddress("127.0.0.1", 9090), rpcMessage);
        System.out.println(o);
    }
}
