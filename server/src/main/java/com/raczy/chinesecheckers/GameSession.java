package com.raczy.chinesecheckers;

import com.raczy.chinesecheckers.builder.BoardBuilder;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import com.raczy.chinesecheckers.exceptions.ForbiddenMoveException;
import com.raczy.chinesecheckers.exceptions.GameException;
import com.raczy.chinesecheckers.mode.GameMode;
import com.raczy.chinesecheckers.mode.StandardGameMode;
import com.raczy.chinesecheckers.util.GraphIDGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by kacperraczy on 12.12.2017.
 */
public class GameSession {
    private static Logger log = LogManager.getLogger(GameSession.class);
    private static GraphIDGenerator idGenerator = new GraphIDGenerator();
    private Random randomGenerator = new Random();

    private int id;
    private ArrayList<Player> players;
    private int expectedPlayerCount;
    private BoardBuilder builder;
    private Board board;
    private GameMode mode;
    private GameSessionState state;

    private ArrayList<GameSessionObserver> observers = new ArrayList<>();

    private int queueCounter;

    public GameSession(int playerNo, BoardBuilder builder) {
        this.id = idGenerator.generate();
        this.expectedPlayerCount = playerNo;
        this.builder = builder;
        setState(GameSessionState.WAITING);
        this.players = new ArrayList<>(playerNo);
    }

    public GameSession(ArrayList<Player> players) {
        this(new StandardBoardBuilder(), players);
    }

    public GameSession(BoardBuilder builder, ArrayList<Player> players) {
        this.id = idGenerator.generate();
        this.players = players;
        this.builder = builder;
        this.expectedPlayerCount = players.size();
        this.start();
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
        boolean opposite = false;
        Iterator<Player> iter = players.iterator();

        builder.generateMainBoardPart();
        for(i = 0; i < 6; i++) {
            builder.generatePlayerBoardPart(i);
        }

        Board board = builder.getResult();

        if(playerCount != 3) {
            i = 0;
            while(iter.hasNext()) {
                player = iter.next();
                player.setZoneID(i);
                board.fillZone(i, player);
                opposite = !opposite;
                if(opposite) {
                    i = Field.oppositeEdge(i);
                }else {
                    i = (i + 1) % 6;
                }
            }
        }else {
            //TODO Implement
            log.error("Not implemented");
            assert false;
        }

        return board;
    }




    public void performMove(GameInfo info, GameSessionCallback callback) {
        if(this.state == GameSessionState.IN_PROGRESS) {
            Player current = getCurrentPlayer();
            if (this.mode.validate(current, info)) {
                update(info);

                boolean playerWon = this.mode.playerStatus(current);
                if (playerWon) {
                    players.remove(current);
                }

                callback.onAccept(playerWon, info);

                nextPlayer();
            } else {
                log.error("Forbidden move by player with id: " + current.getId());
                GameException error = new ForbiddenMoveException();
                callback.onError(error);
            }
        }else {
            log.error("Game is not in progress!");
        }
    }

    private void nextPlayer() {
        queueCounter++;
        notifyTurnChanges();
    }

    private void update(GameInfo info) {
        Field oldf = this.board.getFieldMap().get(info.getOldFieldID());
        Field newf = this.board.getFieldMap().get(info.getNewFieldID());

        Player player = oldf.getChecker();
        newf.setChecker(player);
        oldf.setChecker(null);

        notifyBoardUpdate(info);
    }

    private void start() {
        this.queueCounter = randomGenerator.nextInt(this.getMaxQueue());
        this.board = createBoard(builder, players);
        this.mode = new StandardGameMode(board);
        setState(GameSessionState.IN_PROGRESS);
    }

    private int getMaxQueue() {
        return this.expectedPlayerCount;
    }

    public Player getCurrentPlayer() {
        if (state == GameSessionState.IN_PROGRESS) {
            int q = queueCounter % getMaxQueue();
            //queueCounter = q

            return this.players.get(q);
        }
        return null;
    }

    public void addPlayer(Player p) {
        if(state == GameSessionState.WAITING) {
            this.players.add(p);
            if(this.players.size() == expectedPlayerCount) {
                start();
            }
        }else {
            log.error("Cannot add player. Game is in progress | done.");
        }
    }

    public void addObserver(GameSessionObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(GameSessionObserver observer) {
        this.observers.remove(observer);
    }

    public void notifyStateChanged() {
        for(GameSessionObserver o: observers) {
            o.onStateChange(this, getState());
        }
    }

    public void notifyTurnChanges() {
        for(GameSessionObserver o: observers) {
            o.onPlayerChange(this, getCurrentPlayer());
        }
    }

    public void notifyBoardUpdate(GameInfo info) {
        for(GameSessionObserver o: observers) {
            o.onBoardUpdate(this, info);
        }
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

    public GameSessionState getState() {
        return state;
    }

    private void setState(GameSessionState state) {
        this.state = state;
        notifyStateChanged();
    }

    public int getId() {
        return id;
    }
}
