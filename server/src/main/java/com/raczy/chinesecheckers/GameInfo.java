package com.raczy.chinesecheckers;

import java.util.Date;

/**
 * Created by kacperraczy on 12.12.2017.
 */
public class GameInfo {
    private Date createdAt;
    private int oldFieldID;
    private int newFieldID;

    public GameInfo(Date date, int oldf, int newf) {
        this.createdAt = date;
        this.oldFieldID = oldf;
        this.newFieldID = newf;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getOldFieldID() {
        return oldFieldID;
    }

    public int getNewFieldID() {
        return newFieldID;
    }
}
