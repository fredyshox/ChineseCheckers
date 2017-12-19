package com.raczy.chinesecheckers.exceptions;

import com.raczy.chinesecheckers.exceptions.GameException;

/**
 * Created by kacperraczy on 19.12.2017.
 */
public class ForbiddenMoveException extends GameException {
    public ForbiddenMoveException() {
        this.message = "Forbidden move.";
    }
}
