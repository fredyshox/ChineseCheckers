package com.raczy.server;

import com.google.gson.Gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raczy.chinesecheckers.Player;
import com.raczy.server.message.ErrorMessage;
import com.raczy.server.message.LoginMessage;

import com.raczy.server.message.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by kacperraczy on 29.12.2017.
 */
public class LoginServerHandler extends SimpleChannelInboundHandler<String>{

    private static Logger log = LogManager.getLogger(LoginServerHandler.class);
    private Gson gson = new Gson();
    private LoginServerDelegate delegate;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Connected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String json) throws Exception {
        log.info("Received");
        LoginMessage message = gson.fromJson(json, LoginMessage.class);
        System.out.println(message.toJson());

        if (message != null) {
            log.info("Received login message: " + message.getUsername());

            Channel ch = ctx.channel();
            String id = ctx.channel().id().asShortText();
            int gameID = message.getGameID();
            Player player = new Player(0, message.getUsername());

            if (delegate != null && delegate.playerCanJoin(ctx, player, gameID)) {
                ch.attr(GameServer.PLAYER_KEY).set(player);
                ch.attr(GameServer.GAME_ID).set(message.getGameID());
                next(ctx);

                delegate.playerJoined(ctx, player);
            }else {
                log.error("Unable to join game with id: " + gameID);
                ErrorMessage errorMessage = new ErrorMessage("Unable to join game with id: " + gameID);
                ctx.writeAndFlush(errorMessage.toJson());
            }
        }
    }

    protected void next(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.remove(this);
        GameServerInitializer.applyGameHandler(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public LoginServerDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(LoginServerDelegate delegate) {
        this.delegate = delegate;
    }
}
