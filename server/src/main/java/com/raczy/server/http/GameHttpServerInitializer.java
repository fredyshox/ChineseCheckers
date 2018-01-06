package com.raczy.server.http;

import com.raczy.server.GameHandlerAdapter;
import com.raczy.server.http.GameHttpServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by kacperraczy on 06.01.2018.
 */
public class GameHttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private GameHandlerAdapter adapter;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();

        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());

        GameHttpServerHandler handler = new GameHttpServerHandler();
        handler.setDelegate(this.adapter);
        p.addLast(handler);
    }

    public void setAdapter(GameHandlerAdapter adapter) {
        this.adapter = adapter;
    }

    public GameHandlerAdapter getAdapter() {
        return adapter;
    }
}
