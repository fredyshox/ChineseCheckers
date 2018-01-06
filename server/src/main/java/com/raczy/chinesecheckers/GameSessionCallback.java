package com.raczy.chinesecheckers;

import com.raczy.chinesecheckers.GameInfo;
import com.raczy.chinesecheckers.exceptions.GameException;

/**
 * Used to inform GameSession's user about move validation status
 * Created by kacperraczy on 19.12.2017.
 */
public interface GameSessionCallback {
    void onAccept(Boolean won, GameInfo gameInfo);
    void onError(GameException e);
}
