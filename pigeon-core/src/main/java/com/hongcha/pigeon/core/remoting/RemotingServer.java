package com.hongcha.pigeon.core.remoting;

import com.hongcha.pigeon.core.error.PigeonException;
import com.hongcha.pigeon.core.remoting.protocol.v1.ProtocolDecoder;
import com.hongcha.pigeon.core.remoting.protocol.v1.ProtocolEecoder;
import com.hongcha.pigeon.core.service.metadata.Service;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.lang.reflect.Method;
import java.util.Map;

public class RemotingServer extends AbstractRemoting {

    private final ServerBootstrap serverBootstrap;

    private final EventLoopGroup eventLoopGroupBoss;

    private final EventLoopGroup eventLoopGroupWorker;
    /**
     * 监听的端口
     */
    private final int port;
    /**
     * service handler
     */
    private Map<Service, Object> serviceHandlerMap;

    public RemotingServer(int port, Map<Service, Object> serviceHandlerMap) {
        this.port = port;
        this.serviceHandlerMap = serviceHandlerMap;
        this.eventLoopGroupBoss = new NioEventLoopGroup(1);
        this.eventLoopGroupWorker = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
    }

    public void start() {
        this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupWorker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ProtocolDecoder(), new ProtocolEecoder(), new ServerHandler());
                    }
                });
        try {
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture();
        } catch (InterruptedException e) {
            throw new PigeonException("开启netty server error", e);
        }
    }

    class ServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Object body = null;
            if (msg instanceof RpcMessage) {
                RpcMessage rpcMessage = ((RpcMessage) msg);
                try {
                    Service service = rpcMessage.getService();
                    Object handler = getHandler(service);
                    Object[] params = rpcMessage.getParams();
                    Class<?>[] paramTypes = getParamType(params);
                    Method invokeMethod = handler.getClass().getDeclaredMethod(rpcMessage.getMethodName(), paramTypes);
                    body = invokeMethod.invoke(handler, params);
                } catch (Exception e) {
                    body = e;
                }
                ctx.channel().writeAndFlush(buildRespose(rpcMessage, body));
            }
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

}
