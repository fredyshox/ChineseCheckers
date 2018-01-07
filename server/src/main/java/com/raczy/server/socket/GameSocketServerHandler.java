package com.raczy.server.socket;

import com.google.gson.Gson;
import com.raczy.chinesecheckers.*;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import com.raczy.chinesecheckers.exceptions.GameException;
import com.raczy.server.GameHandlerAdapter;
import com.raczy.server.GameServer;
import com.raczy.server.Utility;
import com.raczy.server.message.*;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler class which is responsible for game traffic and management.
 * It controls multiple game instances at the same time.
 * Created by kacperraczy on 21.12.2017.
 */

@ChannelHandler.Sharable
public class GameSocketServerHandler extends SimpleChannelInboundHandler<String> implements GameSessionObserver, GameHandlerAdapter {

    private static Logger log = LogManager.getLogger(GameSocketServerHandler.class);
    private Gson gson = Utility.getGson();

    //Games
    private Map<Integer, GameSession> games;
    private Map<Integer, ChannelGroup> channelGroups;

    public GameSocketServerHandler() {
        super();

        this.games = new HashMap<>();
        this.channelGroups = new HashMap<>();

        createGame();
    }

    //MARK: LoginServerDelegate


    @Override
    public boolean playerCanJoin(ChannelHandlerContext ctx, Player player, int gameID) {
        GameSession game = games.get(gameID);
        return game.getState() == GameSessionState.WAITING;
    }

    @Override
    public void playerJoined(ChannelHandlerContext ctx, Player player, int gameID) {
        log.info("Player joining with channel id: " + ctx.channel().id());

        GameSession game = games.get(gameID);
        ChannelGroup channelGroup = channelGroups.get(gameID);
        //null safety

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
                addPlayer(player, gameID);
            });
        }
    }

    @Override
    public int createGame(ChannelHandlerContext ctx, int playerNo, String name) {
        GameSession session = new GameSession(playerNo, new StandardBoardBuilder());
        session.setTitle(name);
        this.games.put(session.getId(), session);

        return session.getId();
    }

    @Override
    public GameSession[] availableGames(ChannelHandlerContext ctx) {
        return games.values().toArray(new GameSession[0]);
    }

    ////////

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        int gameID = ctx.channel().attr(GameServer.GAME_ID).get();
        GameSession game = games.get(gameID);

        if (game.getState() == GameSessionState.IN_PROGRESS) {
            log.info("Received move: " + s);
            GameInfo move = gson.fromJson(s, GameInfo.class);
            Player current = game.getCurrentPlayer();
            Player sender = ctx.channel().attr(GameServer.PLAYER_KEY).get();

            if (current.getId() == sender.getId()) {
                if (move != null) {
                    handleMove(ctx, game, move, sender);
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


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);

        Channel ch = ctx.channel();
        int gameId = ch.attr(GameServer.GAME_ID).get();
        Player player = ch.attr(GameServer.PLAYER_KEY).get();

        GameSession session = games.get(gameId);
        if (session != null) {
            session.removePlayer(player);
        }
    }

    private void handleMove(ChannelHandlerContext ctx, GameSession game , GameInfo info, Player player) {
        ChannelGroup channelGroup = channelGroups.get(game.getId());

        game.performMove(info, new GameSessionCallback() {
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

    private void sendCurrentTurnMsg(GameSession game, Player next) {
        ChannelGroup channelGroup = channelGroups.get(game.getId());

        Message msg = new TurnMessage(next.getId());
        channelGroup.writeAndFlush(msg.toJson());
    }

    private void sendInitialMessage(GameSession session) {
        ChannelGroup channelGroup = channelGroups.get(session.getId());

        log.info("Game with id: " + session.getId() + "starts");

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

    private void createGame() {
        GameSession session = new GameSession(2, new StandardBoardBuilder());
        ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        session.addObserver(this);
        this.games.put(session.getId(), session);
        this.channelGroups.put(session.getId(), channelGroup);
    }


    private void addPlayer(Player player, int gameID) {
        GameSession game = games.get(gameID);
        game.addPlayer(player);
    }


    //MARK: GameSessionObserver


    @Override
    public void onStateChange(GameSession session, GameSessionState state) {
        if(state == GameSessionState.IN_PROGRESS) {
            sendInitialMessage(session);
        }
    }

    @Override
    public void onPlayerChange(GameSession session, Player current) {
        sendCurrentTurnMsg(session, current);
    }

    @Override
    public void onBoardUpdate(GameSession session, GameInfo update) {
        //nothing
    }
}

