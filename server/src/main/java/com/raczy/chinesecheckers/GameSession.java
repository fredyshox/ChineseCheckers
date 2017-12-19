package com.raczy.chinesecheckers;

import com.raczy.chinesecheckers.builder.BoardBuilder;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import com.raczy.chinesecheckers.exceptions.ForbiddenMoveException;
import com.raczy.chinesecheckers.exceptions.GameException;
import com.raczy.chinesecheckers.mode.GameMode;
import com.raczy.chinesecheckers.mode.StandardGameMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Created by kacperraczy on 12.12.2017.
 */
public class GameSession {
    private static Logger log = LogManager.getLogger(GameSession.class);
    private Random randomGenerator = new Random();
    private ArrayList<Player> players;
    private Board board;
    private GameMode mode;

    private int queueCounter;

    public GameSession(ArrayList<Player> players) {
        this(new StandardBoardBuilder(), players);
    }

    public GameSession(BoardBuilder builder, ArrayList<Player> players) {
        this.players = players;
        this.board = createBoard(builder, players);
        this.mode = new StandardGameMode(this.board);

        this.queueCounter = randomGenerator.nextInt(this.getMaxQueue());
    }

    private Board createBoard(BoardBuilder builder, ArrayList<Player> players) {
        int playerCount = players.size();
        if (playerCount != 6 && playerCount != 4 && playerCount != 3 && playerCount != 2) {
            //TODO exception
            log.error("Too many players - max 6");
            return null;
        }

        Player player;
        int i;
        Map<Integer, Field> zone;

        builder.generateMainBoardPart();
        if (playerCount != 3) {
            for(i = 0; i<playerCount/2; i++) {
                builder.generatePlayerBoardPart(i);
                builder.generatePlayerBoardPart(Field.oppositeEdge(i));
            }
        }else {
            i = 0;
            do {
                builder.generatePlayerBoardPart(i);
                i = (i + 2) % 6;
            }while(i != 0);
        }

        Board board = builder.getResult();

        for(i = 0; i < board.getPlayerZones().size(); i++) {
            player = players.get(i);
            player.setZoneID(i);
            board.fillZone(i, player);
        }

        return board;
    }




    public void performMove(GameInfo info, GameSessionCallback callback) {
        Player current = getCurrentPlayer();
        if (this.mode.validate(current, info)) {
            update(info);

            boolean playerWon = this.mode.playerStatus(current);
            if (playerWon) {
                players.remove(current);
            }

            queueCounter++;

            callback.onAccept(playerWon, info);
        }else {
            log.error("Forbidden move by player with id: " + current.getId());
            GameException error = new ForbiddenMoveException();
            callback.onError(error);
        }
    }

    private void update(GameInfo info) {
        Field oldf = this.board.getFieldMap().get(info.getOldFieldID());
        Field newf = this.board.getFieldMap().get(info.getNewFieldID());

        Player player = oldf.getChecker();
        newf.setChecker(player);
        oldf.setChecker(null);
    }

    private int getMaxQueue() {
        return this.players.size();
    }

    public Player getCurrentPlayer() {
        int q = queueCounter % getMaxQueue();
        //queueCounter = q

        return this.players.get(q);
    }

    //MARK: Getters/Setters

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }
}
