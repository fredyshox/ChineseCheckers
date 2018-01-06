package com.raczy.server.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by kacperraczy on 06.01.2018.
 */
public class GameHttpServer implements Runnable {

    private int port;
    private GameHttpServerInitializer initializer;

    public GameHttpServer(int port) {
        this.port = port;
    }

    @Override
    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler((initializer == null) ? new GameHttpServerInitializer() : this.initializer);

            Channel ch = b.bind(port).sync().channel();

            ch.closeFuture().sync();

        }catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setInitializer(GameHttpServerInitializer initializer) {
        this.initializer = initializer;
    }

    public GameHttpServerInitializer getInitializer() {
        return initializer;
    }
}
