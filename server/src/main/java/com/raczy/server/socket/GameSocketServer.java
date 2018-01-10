package com.raczy.server.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Responsible for set up and start of socket server.
 * Created by kacperraczy on 06.01.2018.
 */
public class GameSocketServer implements Runnable {

    private int port;
    private GameSocketServerInitializer initializer;

    public GameSocketServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler((initializer == null) ? new GameSocketServerInitializer() : initializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        }catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public GameSocketServerInitializer getInitializer() {
        return initializer;
    }

    public void setInitializer(GameSocketServerInitializer initializer) {
        this.initializer = initializer;
    }
}
