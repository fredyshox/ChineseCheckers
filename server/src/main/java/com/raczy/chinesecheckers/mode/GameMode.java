package com.raczy.chinesecheckers.mode;

/**
 * Created by kacperraczy on 12.12.2017.
 */

import com.raczy.GameInfo;
import com.raczy.Player;

/**
 * @author kacperraczy
 * Strategy interface that declares methods for rule defining object
 */
public interface GameMode {

    /**
     * Perform move validation
     * @param player player perfoming the move
     * @param move actual move
     * @return true if move is legal
     */
    boolean validate(Player player, GameInfo move);
}
