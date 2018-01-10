package com.raczy.chinesecheckers.mode;


import com.raczy.chinesecheckers.GameInfo;
import com.raczy.chinesecheckers.Player;

/**
 * @author kacperraczy
 * Strategy interface that declares methods for rule defining object.
 * Created by kacperraczy on 12.12.2017.
 */
public interface GameMode {

    /**
     * Perform move validation.
     * @param player player who's turn is
     * @param move actual move
     * @return true if move is legal
     */
    boolean validate(Player player, GameInfo move);

    /**
     * Checks if player won the game.
     * @param player player who's turn is
     * @return true if player won
     */
    boolean playerStatus(Player player);
}
