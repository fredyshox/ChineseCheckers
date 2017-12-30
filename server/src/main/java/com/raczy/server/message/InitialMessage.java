package com.raczy.server.message;

import com.google.gson.stream.JsonWriter;
import com.raczy.chinesecheckers.GameSession;
import com.raczy.server.Utility;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by kacperraczy on 29.12.2017.
 */
public class InitialMessage extends Message {

    private GameSession session;

    public InitialMessage(GameSession session){
        super("init");

        this.session = session;
    }

    @Override
    public String toJson() {
        StringWriter strWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(strWriter);
        try {
            writer.beginObject();

            writer.name("type").value(getType());

            String sessionInfo = Utility.sessionToJson(this.session);
            writer.name("session").value(sessionInfo);

            writer.endObject();
            writer.close();
        }catch (IOException ex) {
            log.error(ex.getLocalizedMessage());
        }

        return strWriter.toString();
    }

    public GameSession getSession() {
        return session;
    }
}
