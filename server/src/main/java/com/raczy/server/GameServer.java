package com.raczy.server;

import com.raczy.chinesecheckers.Player;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;

/**
 * Created by kacperraczy on 21.12.2017.
 */
public class GameServer {

    private int port;

    /* Channel Attribute keys */
    public final static AttributeKey<Player> PLAYER_KEY = AttributeKey.valueOf("player");
    public final static AttributeKey<Integer> GAME_ID = AttributeKey.valueOf("gameID");

    public GameServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new GameServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;


        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }else {
            port = 8080;
        }

        new GameServer(port).run();
    }
}
