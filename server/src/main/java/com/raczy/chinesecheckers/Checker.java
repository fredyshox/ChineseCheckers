package com.raczy.chinesecheckers;

/**
 * Created by kacperraczy on 12.12.2017.
 */
public class Checker {
    private int id;
    private int ownerID;

    public Checker(int id, int ownerID) {
        this.id = id;
        this.ownerID = ownerID;
    }

    public int getId() {
        return id;
    }

    public int getOwnerID() {
        return ownerID;
    }
}
