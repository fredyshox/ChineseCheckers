package com.raczy.server.socket;

import com.raczy.server.util.CarriageReturnEncoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Socket channel initializer. Adds necessary encoders/decoders and passes them to appropriate handler.
 * Created by kacperraczy on 21.12.2017.
 */

public class GameSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder(CharsetUtil.UTF_8);
    private static final StringEncoder ENCODER = new CarriageReturnEncoder(CharsetUtil.UTF_8);

    public static final LoginSocketServerHandler LOGIN_HANDLER = new LoginSocketServerHandler();
    public static final GameSocketServerHandler SERVER_HANDLER = new GameSocketServerHandler();

    static {
        LOGIN_HANDLER.setDelegate(SERVER_HANDLER);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        System.out.println("Channel ID: " + ch.id());

        // Add the text line codec combination first,
        pipeline.addLast(new JsonObjectDecoder());
        // the encoder and decoder are static as these are sharable
        pipeline.addLast(DECODER);

        pipeline.addLast(ENCODER);

        // and then business logic.
        pipeline.addLast(LOGIN_HANDLER);
    }

    public static void applyGameHandler(ChannelHandlerContext ctx) {
        ctx.pipeline().addLast(SERVER_HANDLER);
    }
}
