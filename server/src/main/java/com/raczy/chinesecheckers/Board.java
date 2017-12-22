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
    private Map<Integer, Map<Integer, Field>> playerZones;
    private int playerNo;
    private static Logger logger = LogManager.getLogger(Board.class);

    public Board(int playerNo) {
        this.playerNo = playerNo;

        this.fieldMap = new HashMap<Integer, Field>();
        this.playerZones = new HashMap<>(playerNo);
    }

    public void fillZone(int index, Player player) {
        if(index >= this.getPlayerZones().size()) {
            logger.error("Zone with that index does not exist");
        }
        Map<Integer, Field> zone = this.getPlayerZones().get(index);
        for(Field f: zone.values()) {
            f.setChecker(player);
        }
    }

    public Map<Integer, Field> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<Integer, Field> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public Map<Integer, Map<Integer, Field>> getPlayerZones() {
        return playerZones;
    }

    public void setPlayerZones(Map<Integer, Map<Integer, Field>>playerZones) {
        this.playerZones = playerZones;
    }

    public int getPlayerNo() {
        return playerNo;
    }

}
