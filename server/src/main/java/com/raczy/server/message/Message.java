package com.raczy.server.message;

import com.google.gson.*;
import com.raczy.server.Utility;
import com.raczy.utility.JsonRequired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by kacperraczy on 22.12.2017.
 */

public class Message {

    protected static Logger log = LogManager.getLogger(Message.class);

    @JsonRequired
    private final String type;

    public Message(String type) {
        this.type = type;
    }


    public String toJson() {
        Gson gson = Utility.getGson();
        return gson.toJson(this);
    }

    public String getType() {
        return type;
    }
}

