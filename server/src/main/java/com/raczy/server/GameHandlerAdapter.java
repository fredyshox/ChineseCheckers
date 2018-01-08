package com.raczy.server;

import com.raczy.chinesecheckers.session.GameSession;
import com.raczy.chinesecheckers.Player;
import io.netty.channel.ChannelHandlerContext;

/**
 * Adapter class
 * Created by kacperraczy on 05.01.2018.
 */
public interface GameHandlerAdapter {
    void playerJoined(ChannelHandlerContext ctx, Player player, int gameID);
    boolean playerCanJoin(ChannelHandlerContext ctx, Player player, int gameID);
    int createGame(ChannelHandlerContext ctx, int playerNo, String name);
    GameSession[] availableGames(ChannelHandlerContext ctx);
}
