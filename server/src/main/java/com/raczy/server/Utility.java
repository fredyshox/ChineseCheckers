package com.raczy.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.raczy.chinesecheckers.*;
import com.raczy.chinesecheckers.builder.BoardBuilder;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import com.raczy.server.message.GameInfoMessage;
import com.raczy.server.message.LoginMessage;
import com.raczy.server.message.Message;
import com.raczy.utility.AnnotatedDeserializer;
import com.raczy.utility.Iso8601DateTypeAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by kacperraczy on 27.12.2017.
 */
public class Utility {

    protected static Logger log = LogManager.getLogger(Utility.class);

    //test client data generating program
    public static void main(String[] args) {
        BoardBuilder builder = new StandardBoardBuilder();
        builder.generateMainBoardPart();
        builder.generatePlayerBoardPart(0);
        builder.generatePlayerBoardPart(3);
        Board result = builder.getResult();
        Player p0 = new Player(0, "Player1");
        p0.setZoneID(0);
        result.fillZone(0, p0);
        Player p1 = new Player(1, "Player2");
        p1.setZoneID(3);
        result.fillZone(3, p1);

        ArrayList<Player> players = new ArrayList<>();
        players.add(p0);
        players.add(p1);

        String json = Utility.sessionToJson(result, players);

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter("grid.json"));
            out.write(json);
        } catch (Exception ex) {
            System.out.println("ERROR");
        } finally {
            try {
                out.close();
            }catch (Exception e){}
        }
    }



    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GameInfo.class, new AnnotatedDeserializer<GameInfo>());
        builder.registerTypeAdapter(GameInfoMessage.class , new AnnotatedDeserializer<GameInfoMessage>());
        builder.registerTypeAdapter(Message.class, new AnnotatedDeserializer<Message>());
        builder.registerTypeAdapter(LoginMessage.class, new AnnotatedDeserializer<LoginMessage>());
        //builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        builder.registerTypeAdapter(Date.class, new Iso8601DateTypeAdapter());

        return builder.create();
    }

    public static String sessionToJson(GameSession session) {
        return sessionToJson(session.getBoard(), session.getPlayers());
    }

    public static String sessionToJson(Board board, ArrayList<Player> players) {
        Writer strWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(strWriter);
        Gson gson = new Gson();

        try {
            writer.beginObject();
            writer.name("start").value(30);

            writer.name("players");
            String playersJson = gson.toJson(players.toArray());
            writer.jsonValue(playersJson);

            writer.name("fields");
            writer.beginObject();

            Field temp, temp2;
            int i;
            for (Map.Entry<Integer, Field> entrySet : board.getFieldMap().entrySet()) {
                temp = entrySet.getValue();

                writer.name(entrySet.getKey().toString());
                writer.beginObject();
                writer.name("player");
                if (temp.getChecker() != null) {
                    writer.value(temp.getChecker().getId());
                }else {
                    writer.nullValue();
                }
                writer.name("neighbours");
                writer.beginObject();
                for(i = 0; i<temp.getNeighbours().length; i++) {
                    temp2 = temp.getNeighbours()[i];
                    if(temp2 != null) {
                        writer.name(Integer.toString(i)).value(Integer.toString(temp2.getId()));
                    }
                }
                writer.endObject();
                writer.endObject();
            }

            writer.endObject();
            writer.endObject();
            writer.close();
        }catch (Exception e) {
            log.error("Error while serializing game session.", e);
        }

        return strWriter.toString();

    }

}
