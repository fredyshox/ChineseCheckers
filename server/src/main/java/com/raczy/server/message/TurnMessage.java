package com.raczy.server.message;

/**
 * Created by kacperraczy on 22.12.2017.
 */
public class TurnMessage extends Message{

    private int playerID;

    public TurnMessage(int playerID) {
        super("turn");
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }
}
