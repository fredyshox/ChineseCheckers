package com.raczy.chinesecheckers;

import java.util.Arrays;

/**
 * Graph Node class holding info about player's checker presence and references to neighbours.
 * Created by kacperraczy on 12.12.2017.
 */
public class Field {
    public static int TOPRIGHT = 0;
    public static int RIGHT = 1;
    public static int BOTTOMRIGHT = 2;
    public static int BOTTOMLEFT = 3;
    public static int LEFT = 4;
    public static int TOPLEFT = 5;

    private int id;
    private Field[] neighbours;
    private Player player = null;

    public Field(int id) {
        this.id = id;

        Field[] neighbours = new Field[6];
        Arrays.fill(neighbours, null);
        this.neighbours = neighbours;
    }

    public int getId() {
        return id;
    }

    public Field[] getNeighbours() {
        return neighbours;
    }

    public Player getChecker() {
        return player;
    }

    public void setChecker(Player player) {
        this.player = player;
    }

    public boolean isOccupied() {
        return this.getChecker() != null;
    }

    public static int oppositeEdge(int eno) {
        return (eno + 3) % 6;
    }

}