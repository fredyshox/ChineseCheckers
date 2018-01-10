package com.raczy.server.socket;

import com.google.gson.Gson;

import com.raczy.chinesecheckers.Player;
import com.raczy.chinesecheckers.util.GraphIDGenerator;
import com.raczy.server.GameHandlerAdapter;
import com.raczy.server.GameServer;
import com.raczy.server.Utility;
import com.raczy.server.message.ErrorMessage;
import com.raczy.server.message.LoginMessage;

import io.netty.channel.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handler class responsible for logging users and delegating them to desired game instance.
 * Created by kacperraczy on 29.12.2017.
 */
@ChannelHandler.Sharable
public class LoginSocketServerHandler extends SimpleChannelInboundHandler<String>{

    private final static Logger log = LogManager.getLogger(LoginSocketServerHandler.class);
    private final Gson gson = Utility.getGson();
    private final GraphIDGenerator playerIDGenerator = new GraphIDGenerator();
    private GameHandlerAdapter delegate;

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

                delegate.playerJoined(ctx, player, gameID);
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
        GameSocketServerInitializer.applyGameHandler(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public GameHandlerAdapter getDelegate() {
        return delegate;
    }

    public void setDelegate(GameHandlerAdapter delegate) {
        this.delegate = delegate;
    }
}
