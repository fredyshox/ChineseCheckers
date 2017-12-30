package com.raczy.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by kacperraczy on 21.12.2017.
 */
public class GameServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    private static final LoginServerHandler LOGIN_HANDLER = new LoginServerHandler();
    private static final GameServerHandler SERVER_HANDLER = new GameServerHandler();

    static {
        LOGIN_HANDLER.setDelegate(SERVER_HANDLER);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // the encoder and decoder are static as these are sharable
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        // and then business logic.
        pipeline.addLast(LOGIN_HANDLER);
    }

    public static void applyGameHandler(ChannelHandlerContext ctx) {
        ctx.pipeline().addLast(SERVER_HANDLER);
    }
}
