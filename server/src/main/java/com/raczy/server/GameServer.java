package com.raczy.server;

import com.raczy.chinesecheckers.Player;
import com.raczy.server.http.GameHttpServer;
import com.raczy.server.http.GameHttpServerInitializer;
import com.raczy.server.socket.GameSocketServer;
import com.raczy.server.socket.GameSocketServerInitializer;
import io.netty.util.AttributeKey;

import java.util.ArrayList;

/**
 *
 * Created by kacperraczy on 21.12.2017.
 */
public class GameServer {

    private int socketPort = DEFAULT_SOCKET_PORT;
    private int httpPort = DEFAULT_HTTP_PORT;

    private ArrayList<Thread> servers = new ArrayList<>();

    /* Default ports */
    public final static int DEFAULT_HTTP_PORT = 1337;
    public final static int DEFAULT_SOCKET_PORT = 8080;

    /* Channel Attribute keys */
    public final static AttributeKey<Player> PLAYER_KEY = AttributeKey.valueOf("player");
    public final static AttributeKey<Integer> GAME_ID = AttributeKey.valueOf("gameID");

    /* server initializers */
    private final static GameSocketServerInitializer SOCKET_SERVER_INITIALIZER = new GameSocketServerInitializer();
    private final static GameHttpServerInitializer HTTP_SERVER_INITIALIZER = new GameHttpServerInitializer();

    static {
        HTTP_SERVER_INITIALIZER.setAdapter(GameSocketServerInitializer.SERVER_HANDLER);
    }

    public GameServer(){}

    public GameServer(int socketPort) {
        this.socketPort = socketPort;
    }

    public GameServer(int socketPort, int httpPort) {
        this.socketPort = socketPort;
        this.httpPort = httpPort;
    }

    public void run() throws Exception {
        runHttpServer();
        runSocketServer();

        for(Thread t: servers) {
            t.join();
        }
    }

    public void runSocketServer() throws Exception {
        GameSocketServer server = new GameSocketServer(socketPort);
        server.setInitializer(SOCKET_SERVER_INITIALIZER);

        Thread socketThread = new Thread(server);
        socketThread.setName("socketThread");
        socketThread.start();

        servers.add(socketThread);
    }

    public void runHttpServer() throws Exception {
        GameHttpServer server = new GameHttpServer(httpPort);
        server.setInitializer(HTTP_SERVER_INITIALIZER);

        Thread httpThread = new Thread(server);
        httpThread.setName("httpThread");
        httpThread.start();

        servers.add(httpThread);
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
