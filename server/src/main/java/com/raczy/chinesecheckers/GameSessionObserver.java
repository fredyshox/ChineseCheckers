package com.raczy.chinesecheckers;

/**
 * Created by kacperraczy on 23.12.2017.
 */
public interface GameSessionObserver {
    void onStateChange(GameSession session, GameSessionState state);
}
