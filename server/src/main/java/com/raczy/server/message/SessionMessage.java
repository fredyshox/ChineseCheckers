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
    private int playerNo;

    public SessionMessage(int gameID, String title, int playerNo) {
        super("game");

        this.gameID = gameID;
        this.title = title;
        this.playerNo = playerNo;
    }

    public int getGameID() {
        return gameID;
    }

    public int getPlayerNo() {
        return playerNo;
    }

    public String getTitle() {
        return title;
    }
}
