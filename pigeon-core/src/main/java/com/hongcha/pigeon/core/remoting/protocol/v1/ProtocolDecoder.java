package com.hongcha.pigeon.core.remoting.protocol.v1;

import com.hongcha.pigeon.core.remoting.ProtocolConstants;
import com.hongcha.pigeon.core.remoting.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolDecoder.class);

    public ProtocolDecoder() {
        this(ProtocolConstants.MAX_FRAME_LENGTH);
    }


    public ProtocolDecoder(int maxFrameLength) {
        super(maxFrameLength, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            try {
                return decodeFrame(frame);
            } catch (Exception e) {
                LOGGER.error("Decode frame error!", e);
                throw e;
            } finally {
                frame.release();
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf frame) {
        int i = frame.readInt();
        byte[] bytes = new byte[i];
        frame.readBytes(bytes);
        return ObjectSerializable.toObject(bytes, RpcMessage.class);
    }

}
