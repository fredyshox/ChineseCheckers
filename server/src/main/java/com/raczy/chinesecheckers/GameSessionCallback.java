package com.raczy.chinesecheckers;

import com.raczy.chinesecheckers.GameInfo;
import com.raczy.chinesecheckers.exceptions.GameException;

/**
 * Created by kacperraczy on 19.12.2017.
 */
public interface GameSessionCallback {
    void onAccept(Boolean won, GameInfo gameInfo);
    void onError(GameException e);
}
