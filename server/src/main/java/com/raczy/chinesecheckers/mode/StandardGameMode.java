package com.raczy.chinesecheckers.mode;

import com.raczy.GameInfo;
import com.raczy.Player;
import com.raczy.chinesecheckers.Board;
import com.raczy.chinesecheckers.Checker;
import com.raczy.chinesecheckers.Field;
import com.raczy.chinesecheckers.mode.GameMode;

import java.util.*;

/**
 * Standard implementation of GameMode rules object.
 * Created by kacperraczy on 12.12.2017.
 */
public class StandardGameMode implements GameMode {
    private Board board;

    StandardGameMode(Board board) {
        this.board = board;
    }

    public boolean validate(Player player, GameInfo move) {
        if (!validatePresence(move.getOldFieldID())) {
            return false;
        }

        Checker checker = this.board.getFieldMap().get(move.getOldFieldID()).getChecker();
        if (!validateOwnership(player, checker)) {
            return false;
        }

        if(!validateHomeZome(move.getOldFieldID(), move.getNewFieldID())) {
            return false;
        }

        if(!validateMovePath(move.getOldFieldID(), move.getNewFieldID())) {
            return false;
        }

        return true;
    }

    private boolean validateOwnership(Player player, Checker checker) {
        return player.getId() == checker.getOwnerID();
    }

    private boolean validatePresence(int fieldID) {
        Field field = this.board.getFieldMap().get(fieldID);
        if (field == null) {
            return false;
        }

        return field.isOccupied();
    }

    private boolean validateHomeZome(int fromId, int toId) {
        Map<Integer, Field> homeZone = null; //TODO this.board.getPlayerZone etc

        Field field = homeZone.get(fromId);
        if(field == null) {
            return true;
        }

        return (homeZone.get(toId) != null);
    }


    //Single threaded for now
    private boolean validateMovePath(int fromId, int toId) {
        Field from = this.board.getFieldMap().get(fromId);
        Map<Integer, Field> possible = new HashMap<>();

        Arrays.asList(from.getNeighbours()).forEach(field -> {
            if (field != null) {
                possible.put(field.getId(), field);
            }
        });

        for(Map.Entry<Integer, Field> entry: possible.entrySet()) {
            if(validateMove(entry.getValue(), toId, entry.getKey(), false)) {
                return true;
            }
        }

        return false;
    }

    //Basic move validation checks only 1 level deep
    private boolean validateMove(Field field, int destId, int direction, boolean jump) {
        if (field == null) {
            return false;
        }else if (field.getId() == destId) {
            return !field.isOccupied();
        }

        if (!jump) {
            if (field.isOccupied()) {
                    return validateMove(field.getNeighbours()[direction], destId, direction, true);
            }
        }

        return false;
    }

}
