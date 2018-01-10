package com.raczy.server.message;

import com.raczy.utility.JsonRequired;

/**
 * Created by kacperraczy on 29.12.2017.
 */
public class LoginMessage extends Message {

    @JsonRequired
    private final String username;

    @JsonRequired
    private final int gameID;

    public LoginMessage(int gameID, String username) {
        super("login");

        this.gameID = gameID;
        this.username = username;
    }


    public String getUsername() {
        return username;
    }

    public int getGameID() {
        return gameID;
    }
}
