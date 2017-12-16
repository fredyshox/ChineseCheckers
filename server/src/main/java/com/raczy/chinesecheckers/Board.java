package com.raczy.chinesecheckers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Graph class
 * Created by kacperraczy on 10.12.2017.
 */
public class Board {
    private Map<Integer, Field> fieldMap;
    private ArrayList<Map<Integer, Field>> playerZones;
    private int playerNo;
    static Logger logger = LogManager.getLogger();

    public Board(int playerNo) {
        this.playerNo = playerNo;

        this.fieldMap = new HashMap<Integer, Field>();
        this.playerZones = new ArrayList<Map<Integer, Field>>(playerNo);
    }


    public Map<Integer, Field> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<Integer, Field> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public ArrayList<Map<Integer, Field>> getPlayerZones() {
        return playerZones;
    }

    public void setPlayerZones(ArrayList<Map<Integer, Field>> playerZones) {
        this.playerZones = playerZones;
    }

    public int getPlayerNo() {
        return playerNo;
    }

}
