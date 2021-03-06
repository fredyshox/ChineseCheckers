package com.raczy.chinesecheckers.mode;

import com.raczy.chinesecheckers.GameInfo;
import com.raczy.chinesecheckers.Player;
import com.raczy.chinesecheckers.Board;
import com.raczy.chinesecheckers.Field;

import java.util.*;

/**
 * Standard implementation of GameMode rules object.
 * Created by kacperraczy on 12.12.2017.
 */
public class StandardGameMode implements GameMode {
    private final Board board;

    public StandardGameMode(Board board) {
        this.board = board;
    }

    @Override
    public boolean playerStatus(Player player) {
        int zoneID = Field.oppositeEdge(player.getZoneID()); //?
        Map<Integer, Field> playerZone = this.board.getPlayerZones().get(zoneID);
        boolean result = true;

        for(Field f: playerZone.values()) {
            if(f.getChecker() == null) {
                result = false;
                break;
            }else {
                Player p = f.getChecker();
                if(p.getId() != player.getId()) {
                    result = false;
                    break;
                }
            }
        }



        return result;
    }

    @Override
    public boolean validate(Player player, GameInfo move) {
        if (!validatePresence(move.getOldFieldID())) {
            return false;
        }

        Player checker = this.board.getFieldMap().get(move.getOldFieldID()).getChecker();
        if (!validateOwnership(player, checker)) {
            return false;
        }

        if(!validateHomeZome(player, move.getOldFieldID(), move.getNewFieldID())) {
            return false;
        }

        if(!validateMovePath(move.getOldFieldID(), move.getNewFieldID())) {
            return false;
        }

        return true;
    }

    private boolean validateOwnership(Player player, Player checker) {
        return player.getId() == checker.getId();
    }

    private boolean validatePresence(int fieldID) {
        Field field = this.board.getFieldMap().get(fieldID);
        if (field == null) {
            return false;
        }

        return field.isOccupied();
    }

    private boolean validateHomeZome(Player player, int fromId, int toId) {
        int oppositeZone = Field.oppositeEdge(player.getZoneID());
        Map<Integer, Field> homeZone = this.board.getPlayerZones().get(oppositeZone);

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
        Field field;

        for(int i = 0; i<from.getNeighbours().length; i++) {
            field = from.getNeighbours()[i];
            if (field != null) {
                possible.put(i, field);
            }
        }

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
