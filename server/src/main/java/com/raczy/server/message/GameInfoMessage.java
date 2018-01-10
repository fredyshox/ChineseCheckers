package com.raczy.server.message;

import com.raczy.chinesecheckers.GameInfo;
import com.raczy.utility.JsonRequired;

/**
 * Created by kacperraczy on 29.12.2017.
 */
public class GameInfoMessage extends Message {

    @JsonRequired
    private final GameInfo info;

    public GameInfoMessage(final GameInfo info) {
        super("info");
        this.info = info;
    }

    public GameInfo getInfo() {
        return info;
    }
}
