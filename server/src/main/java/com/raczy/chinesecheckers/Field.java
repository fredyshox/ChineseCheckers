package com.raczy.chinesecheckers;

import java.util.Arrays;

/**
 * Graph Node class holding info about player's checker presence and references to neighbours.
 * Created by kacperraczy on 12.12.2017.
 */
public class Field {
    public final static int TOPRIGHT = 0;
    public final static int RIGHT = 1;
    public final static int BOTTOMRIGHT = 2;
    public final static int BOTTOMLEFT = 3;
    public final static int LEFT = 4;
    public final static int TOPLEFT = 5;

    public final static int NEIGHBOUR_COUNT = 6;

    private final int id;
    private Field[] neighbours;
    private Player player = null;

    public Field(final int id) {
        this.id = id;

        Field[] neighbours = new Field[NEIGHBOUR_COUNT];
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
        return (eno + 3) % NEIGHBOUR_COUNT;
    }

}
