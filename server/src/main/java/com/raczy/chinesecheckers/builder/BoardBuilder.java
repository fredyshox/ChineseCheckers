package com.raczy.chinesecheckers.builder;

import com.raczy.chinesecheckers.util.GraphIDGenerator;
import com.raczy.chinesecheckers.Board;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for building boards objects.
 * Created by kacperraczy on 11.12.2017.
 */
public abstract class BoardBuilder {
    static Logger logger = LogManager.getLogger();
    protected GraphIDGenerator gen = new GraphIDGenerator();

    public abstract void generateMainBoardPart();
    public abstract void generatePlayerBoardPart(int side);

    public abstract Board getResult();

}
