package com.raczy.server.util;

import com.sun.istack.internal.NotNull;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * StringEncoder subclass which adds carriage return delimiter to the end of message.
 * Created by kacperraczy on 03.01.2018.
 */

@ChannelHandler.Sharable
public class CarriageReturnEncoder extends StringEncoder {

    public CarriageReturnEncoder(@NotNull Charset charset) {
        super(charset);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        String str = msg.toString() + "\r\n";
        super.encode(ctx, str, out);
    }
}
