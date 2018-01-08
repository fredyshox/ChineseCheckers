package com.raczy.chinesecheckers.session;

import com.raczy.chinesecheckers.Board;
import com.raczy.chinesecheckers.Field;
import com.raczy.chinesecheckers.Player;
import com.raczy.chinesecheckers.builder.BoardBuilder;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import com.raczy.chinesecheckers.mode.GameMode;
import com.raczy.chinesecheckers.mode.StandardGameMode;
import com.raczy.chinesecheckers.session.GameSession;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Standard implementation of GameSession using StandardBoardBuilder and StandardGameMode.
 * Created by kacperraczy on 12.12.2017.
 */
public class StandardGameSession extends GameSession {

    // MARK: Init

    public StandardGameSession(int playerNo) {
        super(playerNo, new StandardBoardBuilder());
    }

    public StandardGameSession(ArrayList<Player> players) {
        super(new StandardBoardBuilder(), players);
    }

    // MARK: Superclass methods

    @Override
    protected Board createBoard(BoardBuilder builder, ArrayList<Player> players) {
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
            i = 0;
            while(iter.hasNext()) {
                player = iter.next();
                player.setZoneID(i);
                board.fillZone(i, player);
                i = (i + 2) % 6;
            }
        }

        return board;
    }

    @Override
    protected GameMode createMode() {
        return new StandardGameMode(this.getBoard());
    }
}
