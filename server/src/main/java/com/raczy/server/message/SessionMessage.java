package com.raczy.server.message;

import com.raczy.utility.JsonRequired;

/**
 * Created by kacperraczy on 05.01.2018.
 */
public class SessionMessage extends Message {

    private int gameID;

    @JsonRequired
    private String title;

    @JsonRequired
    private int playerCount;

    private int currentPlayerCount;

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
