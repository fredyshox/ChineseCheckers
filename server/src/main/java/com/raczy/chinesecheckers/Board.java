package com.raczy.chinesecheckers;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Class representing game board holding references to all fields.
 * The referenced Field objects are creating graph structure.
 * Created by kacperraczy on 10.12.2017.
 */
public class Board {

    private final static Logger logger = LogManager.getLogger(Board.class);

    private Map<Integer, Field> fieldMap;
    private Map<Integer, Map<Integer, Field>> playerZones;
    private final int playerNo;

    public Board(int playerNo) {
        this.playerNo = playerNo;

        this.fieldMap = new HashMap<Integer, Field>();
        this.playerZones = new HashMap<>(playerNo);
    }

    public void fillZone(int index, Player player) {
        Map<Integer, Field> zone = this.getPlayerZones().get(index);
        if (zone != null) {
            for (Field f : zone.values()) {
                f.setChecker(player);
            }
        }else {
            logger.error("Zone with that index does not exist. Index = " + index);
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
