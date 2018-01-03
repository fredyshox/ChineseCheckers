package com.raczy.server;

import com.google.gson.Gson;
import com.raczy.chinesecheckers.*;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import com.raczy.chinesecheckers.exceptions.GameException;
import com.raczy.server.message.*;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kacperraczy on 21.12.2017.
 */

@ChannelHandler.Sharable
public class GameServerHandler extends SimpleChannelInboundHandler<String> implements GameSessionObserver, LoginServerDelegate {

    private static Logger log = LogManager.getLogger(GameServerHandler.class);
    private Gson gson = Utility.getGson();

    //TODO multiple games
    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //TODO game options
    private GameSession game;
    private int expectedPlayerNo = 2;

    public GameServerHandler() {
        super();

        this.game = new GameSession(expectedPlayerNo, new StandardBoardBuilder());
        this.game.addObserver(this);
    }

    //MARK: LoginServerDelegate


    @Override
    public boolean playerCanJoin(ChannelHandlerContext ctx, Player player, int gameID) {
        return this.game.getState() == GameSessionState.WAITING;
    }

    @Override
    public void playerJoined(ChannelHandlerContext ctx, Player player) {
        log.info("Player joining with channel id: " + ctx.channel().id());

        if (game.getState() == GameSessionState.WAITING) {
            Channel ch = ctx.channel();
            channelGroup.add(ch);

            Map<String, Object> json = new HashMap<>();
            json.put("id", player.getId());
            json.put("username", player.getUsername());
            json.put("type", "player");
            String json_str = gson.toJson(json);
            log.info(json_str);
            ChannelFuture future = ctx.writeAndFlush(json_str);

            future.addListener((chFuture) -> {
                log.info("Data sent");
                addPlayer(player);
            });
        }
    }

    ////////

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        if (game.getState() == GameSessionState.IN_PROGRESS) {
            log.info("Received move: " + s);
            GameInfo move = gson.fromJson(s, GameInfo.class);
            Player current = game.getCurrentPlayer();
            Player sender = ctx.channel().attr(GameServer.PLAYER_KEY).get();

            if (current.getId() == sender.getId()) {
                if (move != null) {
                    handleMove(ctx, move, sender);
                } else {
                    log.error("Unable to parse GameInfo json");
                }
            } else {
                log.error("Invalid player: " + sender.getId());
                Message msg = new ErrorMessage("Invalid player: " + sender.getId());
                ctx.write(msg.toJson());
                ctx.flush();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    private void handleMove(ChannelHandlerContext ctx, GameInfo info, Player player) {
        this.game.performMove(info, new GameSessionCallback() {
            @Override
            public void onAccept(Boolean won, GameInfo gameInfo) {
                GameInfoMessage message = new GameInfoMessage(gameInfo);
                String json = message.toJson();
                ChannelGroupFuture future = channelGroup.writeAndFlush(json);

                if(won) {
                    //session listener for this?
                    Message msg = new ResultMessage(player.getId(), ResultMessage.Result.WON);
                    channelGroup.writeAndFlush(msg.toJson());
                }
            }

            @Override
            public void onError(GameException e) {
                log.error("Catched game exception", e);
                Message msg = new ErrorMessage(e.getMessage());
                ctx.write(msg.toJson());
                ctx.flush();
            }
        });
    }

    private void sendCurrentTurnMsg(Player next) {
        Message msg = new TurnMessage(next.getId());
        channelGroup.writeAndFlush(msg.toJson());
    }

    private void sendInitialMessage(GameSession session) {
        log.info("Game starts");
        InitialMessage initMessage = new InitialMessage(session);
        ChannelGroupFuture future = channelGroup.writeAndFlush(initMessage.toJson());

        future.addListener((channelFutures) -> {
            assert channelFutures == future;

            Player current = session.getCurrentPlayer();
            log.info("Turn for player with id: " + current.getId());
            Message turnMessage = new TurnMessage(current.getId());
            channelGroup.writeAndFlush(turnMessage.toJson());
        });

    }


    private void addPlayer(Player player) {
        this.game.addPlayer(player);
    }


    //MARK: GameSessionObserver


    @Override
    public void onStateChange(GameSession session, GameSessionState state) {
        if(state == GameSessionState.IN_PROGRESS) {
            sendInitialMessage(this.game);
        }
    }

    @Override
    public void onPlayerChange(GameSession session, Player current) {
        sendCurrentTurnMsg(current);
    }

    @Override
    public void onBoardUpdate(GameSession session, GameInfo update) {
        //nothing
    }
}

