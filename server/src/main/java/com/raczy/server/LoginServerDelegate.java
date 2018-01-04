package com.raczy.server;

import com.raczy.chinesecheckers.Player;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by kacperraczy on 30.12.2017.
 */
public interface LoginServerDelegate {
    void playerJoined(ChannelHandlerContext ctx, Player player, int gameID);
    boolean playerCanJoin(ChannelHandlerContext ctx, Player player, int gameID);
}
