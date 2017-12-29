package com.raczy.chinesecheckers;

/**
 * Created by kacperraczy on 23.12.2017.
 */
public interface GameSessionObserver {
    void onStateChange(GameSession session, GameSessionState state);
    void onPlayerChange(GameSession session, Player current);
    void onBoardUpdate(GameSession session, GameInfo update);
}
