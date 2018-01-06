package com.raczy.server.http;

import com.google.gson.Gson;
import com.oracle.tools.packager.Log;
import com.raczy.chinesecheckers.GameSession;
import com.raczy.server.GameHandlerAdapter;
import com.raczy.server.Utility;
import com.raczy.server.message.ErrorMessage;
import com.raczy.server.message.SessionMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Handler class that manages REST API interface for managing games.
 * Enables game creation and listing available games.
 * Created by kacperraczy on 04.01.2018.
 */
public class GameHttpServerHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger log = LogManager.getLogger(GameHttpServerHandler.class);
    private static  Gson gson = Utility.getGson();
    private GameHandlerAdapter delegate;

    private HttpRequest request;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            this.request = request;

        } else if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            String uri = request.uri();

            HttpMethod method = request.method();

            log.info("Uri: " + uri);

            if (uri.equals("/games")) {
                if (method == HttpMethod.GET) {
                    handleGetGames(ctx, content);
                } else if (method == HttpMethod.POST) {
                    handlePostGames(ctx, content);
                } else {
                    handleUnsupported(ctx);
                }
            }else {
                handleUnsupported(ctx);
            }

            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private GameSession[] getAvailableGames(ChannelHandlerContext ctx) {
        if (delegate == null) {
            return null;
        }else {
            return delegate.availableGames(ctx);
        }
    }

    private boolean createGameSession(ChannelHandlerContext ctx, int playerNo, String name) {
        if (delegate == null) {
            return false;
        }
        delegate.createGame(ctx, playerNo ,name);
        return  true;
    }

    private void handlePostGames(ChannelHandlerContext ctx, HttpContent content) {
        String responseJson = "";
        HttpResponseStatus status = HttpResponseStatus.OK;

        ByteBuf bodyBuf = content.content();
        if (bodyBuf.isReadable()) {
            String body = bodyBuf.toString(CharsetUtil.UTF_8);
            SessionMessage sessionMsg = gson.fromJson(body, SessionMessage.class);
            if (sessionMsg != null) {
                if (createGameSession(ctx, sessionMsg.getPlayerCount(), sessionMsg.getTitle())) {
                    responseJson = "{\"status\": \"OK\"}";
                }else {
                    ErrorMessage err = new ErrorMessage("Unable to reach socket server");
                    status = HttpResponseStatus.BAD_REQUEST;
                    responseJson = err.toJson();
                }
            }else {
                ErrorMessage err = new ErrorMessage("Unable to deserialize Session message");
                status = HttpResponseStatus.BAD_REQUEST;
                responseJson = err.toJson();
            }
        }

        writeResponse(status, responseJson, ctx);
    }

    private void handleGetGames(ChannelHandlerContext ctx, HttpContent content) {
        String responseJson;
        HttpResponseStatus status = HttpResponseStatus.OK;

        GameSession[] games = getAvailableGames(ctx);
        if (games != null) {
            ArrayList<SessionMessage> sessionArr = new ArrayList<>();
            for (GameSession game: games) {
                int currentPlayerCount = game.getPlayers().size();
                sessionArr.add(new SessionMessage(game.getId(), game.getTitle(), game.getExpectedPlayerCount(), currentPlayerCount));
            }
            responseJson = gson.toJson(sessionArr);
        }else {
            ErrorMessage err = new ErrorMessage("Unable to reach socket server");
            status = HttpResponseStatus.BAD_REQUEST;
            responseJson = err.toJson();
        }

        writeResponse(status, responseJson, ctx);
    }

    private void handleUnsupported(ChannelHandlerContext ctx) {
        ErrorMessage err = new ErrorMessage("Unsupported uri.");
        String responseJson = err.toJson();
        writeResponse(HttpResponseStatus.BAD_REQUEST, responseJson, ctx);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void writeResponse(HttpResponseStatus status, String json, ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF8");

        Log.info("Writing http response.");
        ctx.writeAndFlush(response);
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
