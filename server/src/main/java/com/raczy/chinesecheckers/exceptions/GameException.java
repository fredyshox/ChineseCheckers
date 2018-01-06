package com.raczy.chinesecheckers.exceptions;

/**
 * Base class for in-game exceptions
 * Created by kacperraczy on 19.12.2017.
 */
public abstract class GameException extends Exception{
    protected String message;

    @Override
    public String getMessage() {
        return this.message;
    }
}
