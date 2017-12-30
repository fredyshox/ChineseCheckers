package com.raczy.server.message;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by kacperraczy on 22.12.2017.
 */
public class Message {

    private final String type;
    protected static Logger log = LogManager.getLogger(Message.class);

    public Message(String type) {
        this.type = type;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getType() {
        return type;
    }
}
