package com.hongcha.pigeon.core.remoting;

import com.hongcha.pigeon.core.remoting.protocol.v1.ProtocolDecoder;
import com.hongcha.pigeon.core.remoting.protocol.v1.ProtocolEecoder;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RemotingClient extends AbstractRemoting {
    private final Map<ServiceAddress, Channel> serviceAddressBootstrapMap = new ConcurrentHashMap<>();
    private final EventLoopGroup eventLoopGroupWorker;
    private final ClientHandler clientHandler;
    private final Bootstrap bootstrap;

    public RemotingClient() {
        eventLoopGroupWorker = new NioEventLoopGroup();
        clientHandler = new ClientHandler();
        bootstrap = new Bootstrap();
        bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class).option(
                ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ProtocolDecoder(), new ProtocolEecoder(), clientHandler);
                    }
                });
    }

    public Object sendSync(ServiceAddress serviceAddress, Service service, String methodName, Object[] params) throws InterruptedException {
        return sendSync(serviceAddress, buildRequest(service, methodName, params));
    }

    public Object sendSync(ServiceAddress serviceAddress, RpcMessage rpcMessage) throws InterruptedException {
        Channel channel = serviceAddressBootstrapMap.get(serviceAddress);
        if (channel == null || !channel.isActive()) {
            if (channel != null) channel.close().sync();
            serviceAddressBootstrapMap.remove(serviceAddress);
            ChannelFuture future = bootstrap.connect(serviceAddress.getIp(), serviceAddress.getPort()).sync();
            channel = future.channel();
            channel.closeFuture();
            serviceAddressBootstrapMap.put(serviceAddress, channel);
        }
        return super.sendSync(channel, rpcMessage);
    }

    @ChannelHandler.Sharable
    class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof RpcMessage) {
                RpcMessage resp = (RpcMessage) msg;
                int id = resp.getId();
                MessageFuture messageFuture = messageFutureMap.remove(id);
                if (messageFuture != null) {
                    Object body = resp.getBody();
                    CompletableFuture<Object> messageFuture1 = messageFuture.getMessageFuture();
                    messageFuture1.complete(body);
                }
            }
        }
    }

}
