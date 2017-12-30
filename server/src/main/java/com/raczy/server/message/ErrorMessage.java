package com.raczy.server.message;

/**
 * Created by kacperraczy on 23.12.2017.
 */
public class ErrorMessage extends Message {

    private String cause;

    public ErrorMessage(String msg) {
        super("error");
        this.cause = msg;
    }

    public String getCause() {
        return cause;
    }
}
