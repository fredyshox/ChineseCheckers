package com.raczy.chinesecheckers;

import com.raczy.utility.JsonRequired;

import java.util.Date;

/**
 * Data class holding information about move
 * Created by kacperraczy on 12.12.2017.
 */
public class GameInfo {
    @JsonRequired
    private final Date createdAt;
    @JsonRequired
    private final int oldFieldID;
    @JsonRequired
    private final int newFieldID;

    public GameInfo(Date date, int oldf, int newf) {
        this.createdAt = new Date(date.getTime());
        this.oldFieldID = oldf;
        this.newFieldID = newf;
    }

    public Date getCreatedAt() {
        return new Date(createdAt.getTime());
    }

    public int getOldFieldID() {
        return oldFieldID;
    }

    public int getNewFieldID() {
        return newFieldID;
    }
}
