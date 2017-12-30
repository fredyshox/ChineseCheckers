package com.raczy.server.message;

/**
 * Created by kacperraczy on 22.12.2017.
 */
public class ResultMessage extends Message {

    public enum Result {
        WON, LOST
    }

    private int playerID;
    private Result result;

    public ResultMessage(int playerID, Result result) {
        super("result");
        this.playerID = playerID;
        this.result = result;
    }

    public int getPlayerID() {
        return playerID;
    }

    public Result getResult() {
        return result;
    }
}
