package com.raczy.server;

import com.google.gson.Gson;

import com.raczy.chinesecheckers.Player;
import com.raczy.chinesecheckers.util.GraphIDGenerator;
import com.raczy.server.message.ErrorMessage;
import com.raczy.server.message.LoginMessage;

import com.raczy.server.message.Message;
import io.netty.channel.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by kacperraczy on 29.12.2017.
 */
@ChannelHandler.Sharable
public class LoginServerHandler extends SimpleChannelInboundHandler<String>{

    private static Logger log = LogManager.getLogger(LoginServerHandler.class);
    private Gson gson = Utility.getGson();
    private GraphIDGenerator playerIDGenerator = new GraphIDGenerator();
    private LoginServerDelegate delegate;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Connected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String json) throws Exception {
        LoginMessage message = gson.fromJson(json, LoginMessage.class);
        log.info("reveived sth");
        if (message != null) {
            log.info("Received login message: " + message.getUsername() + ", message: " + message.toJson());

            Channel ch = ctx.channel();
            int id = ch.hashCode();
            int gameID = message.getGameID();
            Player player = new Player(id, message.getUsername());

            if (delegate != null && delegate.playerCanJoin(ctx, player, gameID)) {
                ch.attr(GameServer.PLAYER_KEY).set(player);
                ch.attr(GameServer.GAME_ID).set(gameID);
                next(ctx);

                delegate.playerJoined(ctx, player);
            } else {
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
