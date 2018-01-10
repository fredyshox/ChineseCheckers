package com.raczy.chinesecheckers.util;

/**
 * Created by kacperraczy on 11.12.2017.
 */
public class GraphIDGenerator {
    private int current;

    public GraphIDGenerator() {
        this.current = -1;
    }

    public int generate() {
        current += 1;
        return current;
    }
}
