package com.hongcha.pigeon.core.remoting.protocol.v1;

import com.hongcha.pigeon.core.error.PigeonException;
import com.hongcha.pigeon.core.remoting.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolEecoder extends MessageToByteEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolEecoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage rpcMessage = (RpcMessage) msg;
                checkParamSerializable(rpcMessage);
                try {
                    checkBodySerializable(rpcMessage);
                } catch (Exception e) {
                    rpcMessage.setBody(e);
                }
                byte[] bytes = ObjectSerializable.toByteArray(rpcMessage);
                int length = bytes.length;
                out.writeInt(length);
                out.writeBytes(bytes);
            } else {
                throw new UnsupportedOperationException("Not support this class:" + msg.getClass());
            }
        } catch (Throwable e) {
            LOGGER.error("Encode request error!", e);
        }
    }

    private void checkBodySerializable(RpcMessage rpcMessage) {
        Object body = rpcMessage.getBody();
        if (body != null) {
            if (body instanceof Throwable) return;
            if (!ObjectSerializable.isSerializable(body)) {
                throw new PigeonException("class : " + body.getClass() + "no impl Serializable");
            }
        }
    }

    private void checkParamSerializable(RpcMessage rpcMessage) {
        Object[] params = rpcMessage.getParams();
        if (params != null) {
            for (Object param : params) {
                if (!ObjectSerializable.isSerializable(param)) {
                    throw new PigeonException("class : " + param.getClass() + "no impl Serializable");
                }
            }
        }
    }
}
