package com.raczy.server.message;

import com.raczy.utility.JsonRequired;

/**
 * Created by kacperraczy on 05.01.2018.
 */
public class SessionMessage extends Message {

    private final int gameID;

    @JsonRequired
    private final String title;

    @JsonRequired
    private final int playerCount;

    private final int currentPlayerCount;

    public SessionMessage(int gameID, String title, int playerNo, int currentPlayerNo) {
        super("game");

        this.gameID = gameID;
        this.title = title;
        this.playerCount = playerNo;
        this.currentPlayerCount = currentPlayerNo;
    }

    public int getGameID() {
        return gameID;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getCurrentPlayerCount() {
        return currentPlayerCount;
    }

    public String getTitle() {
        return title;
    }
}
