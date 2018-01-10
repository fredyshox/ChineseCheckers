package com.raczy.chinesecheckers.session;

import com.raczy.chinesecheckers.*;
import com.raczy.chinesecheckers.builder.BoardBuilder;
import com.raczy.chinesecheckers.exceptions.ForbiddenMoveException;
import com.raczy.chinesecheckers.exceptions.GameException;
import com.raczy.chinesecheckers.mode.GameMode;
import com.raczy.chinesecheckers.util.GraphIDGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main class for creating and managing ChineseCheckers session.
 * It creates board with provided builder class and validates moves using GameMove object.
 * Also responsible for turn management.
 * Created by kacperraczy on 08.01.2018.
 */
public abstract class GameSession {
    protected final static Logger log = LogManager.getLogger(GameSession.class);
    private final static GraphIDGenerator idGenerator = new GraphIDGenerator();
    private final Random randomGenerator = new Random();


    //MARK: Properties

    private final int id;
    private String title;

    private final ArrayList<Player> players;
    private final BoardBuilder builder;
    private int expectedPlayerCount;
    private Board board;
    private GameMode mode;
    private GameSessionState state;

    private final ArrayList<GameSessionObserver> observers = new ArrayList<>();

    private int queueCounter;

    // MARK: Abstract

    /**
     * Initializes and return Board object built using provided builder class.
     * Apart from board creation this method handles checkers distribution.
     * @param builder builder object
     * @param players players participating
     * @return created Board instance
     */

    abstract protected Board createBoard(BoardBuilder builder, ArrayList<Player> players);

    /**
     * Initializes and returns GameMode object
     * @return GameMode object
     */
    abstract protected GameMode createMode();


    // MARK: Initialization

    public GameSession(int playerNo, BoardBuilder builder) {
        this.id = idGenerator.generate();
        this.expectedPlayerCount = playerNo;
        this.builder = builder;
        this.players = new ArrayList<>(playerNo);
        setState(GameSessionState.WAITING);
    }

    public GameSession(BoardBuilder builder, ArrayList<Player> players) {
        this.id = idGenerator.generate();
        this.players = players;
        this.builder = builder;
        this.expectedPlayerCount = players.size();
        this.start();
    }


    // MARK:

    public void performMove(GameInfo info, GameSessionCallback callback) {
        if(this.state == GameSessionState.IN_PROGRESS) {
            Player current = getCurrentPlayer();
            if (this.mode.validate(current, info)) {
                update(info);

                boolean playerWon = this.mode.playerStatus(current);
                if (playerWon) {
                    removePlayer(current);
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

    protected void nextPlayer() {
        queueCounter++;
        notifyTurnChanges();
    }

    protected void update(GameInfo info) {
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
        this.mode = createMode();
        setState(GameSessionState.IN_PROGRESS);
    }



    // MARK: Player management

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

    public void removePlayer(Player p) {
        this.players.remove(p);
        this.expectedPlayerCount--;
        if (state == GameSessionState.IN_PROGRESS) {
            if (this.players.size() < 2) {
                setState(GameSessionState.DONE);
            }
        }
    }

    // MARK: Observers

    public void addObserver(GameSessionObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(GameSessionObserver observer) {
        this.observers.remove(observer);
    }

    protected void notifyStateChanged() {
        for(GameSessionObserver o: observers) {
            o.onStateChange(this, getState());
        }
    }

    protected void notifyTurnChanges() {
        Player current = getCurrentPlayer();
        if (current != null) {
            for(GameSessionObserver o: observers) {
                o.onPlayerChange(this, getCurrentPlayer());
            }
        }
    }

    protected void notifyBoardUpdate(GameInfo info) {
        for(GameSessionObserver o: observers) {
            o.onBoardUpdate(this, info);
        }
    }


    // MARK: Getters/Setters

    public GameMode getMode() {
        return mode;
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

    protected void setState(GameSessionState state) {
        this.state = state;
        notifyStateChanged();
    }

    public int getId() {
        return id;
    }

    public int getExpectedPlayerCount() {
        return expectedPlayerCount;
    }

    public String getTitle() {
        return (title != null) ? title : "Game" + id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
