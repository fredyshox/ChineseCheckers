package board;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by kacperraczy on 14.12.2017.
 */


@RunWith(Suite.class)
@Suite.SuiteClasses({
        EdgeLR_BoardTest.class,
        EdgeBLTR_BoardTest.class,
        EdgeBRTL_BoardTest.class,
        PlayerZones_BoardTest.class
})

public class BoardTestSuite {
    //empty class used only as placeholder for annotations
}
