package com.raczy.server.message;

import com.raczy.chinesecheckers.GameInfo;

/**
 * Created by kacperraczy on 29.12.2017.
 */
public class GameInfoMessage extends Message {

    private GameInfo info;

    public GameInfoMessage(GameInfo info) {
        super("info");
        this.info = info;
    }

    public GameInfo getInfo() {
        return info;
    }
}
