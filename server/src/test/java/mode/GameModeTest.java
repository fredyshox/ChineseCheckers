package mode;

import com.raczy.chinesecheckers.Board;
import com.raczy.chinesecheckers.Field;
import com.raczy.chinesecheckers.GameInfo;
import com.raczy.chinesecheckers.Player;
import com.raczy.chinesecheckers.builder.BoardBuilder;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import com.raczy.chinesecheckers.mode.GameMode;
import com.raczy.chinesecheckers.mode.StandardGameMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created by kacperraczy on 09.01.2018.
 */
public class GameModeTest {

    private GameMode mode;

    private Board board;
    private Player player;

    @Before
    public void initialize() {
        BoardBuilder builder = new StandardBoardBuilder();
        builder.generateMainBoardPart();
        builder.generatePlayerBoardPart(0);
        builder.generatePlayerBoardPart(3);
        Board b = builder.getResult();

        board = b;
        mode = new StandardGameMode(b);

        player = new Player(0, "test");
    }

    @Test
    public void test_MoveValidation() {
        board.getFieldMap().get(5).setChecker(player);
        board.getFieldMap().get(0).setChecker(player);
        board.getFieldMap().get(1).setChecker(player);
        board.getFieldMap().get(2).setChecker(player);
        board.getFieldMap().get(12).setChecker(player);

        int expectedPossible = 3;

        Field start = board.getFieldMap().get(0);
        int actual = getPossibleMoveCount(start);

        Assert.assertEquals(expectedPossible, actual);
    }

    @Test
    public void test_MoveValidation_withZones() {
        player.setZoneID(3);

        Field start = board.getFieldMap().get(0).getNeighbours()[Field.TOPRIGHT];
        start.getNeighbours()[Field.RIGHT].setChecker(player);
        start.setChecker(player);

        int expectedPossible = 2;
        int actual = getPossibleMoveCount(start);

        Assert.assertEquals(expectedPossible, actual);
    }

    private int getPossibleMoveCount(Field start) {
        Field temp;
        GameInfo info;
        int actual = 0;
        for(int i = 0; i<6; i++) {
            temp = start.getNeighbours()[i];
            if (temp != null) {
                info = new GameInfo(new Date(), start.getId(), temp.getId());
                if(mode.validate(player, info)) {
                    actual++;
                }

                temp = temp.getNeighbours()[i];
                if (temp != null) {
                    info = new GameInfo(null, start.getId(), temp.getId());
                    if (mode.validate(player, info)) {
                        actual++;
                    }
                }
            }
        }

        return actual;
    }


    @Test
    public void test_playerStatus() {
        board.fillZone(0, player);
        player.setZoneID(3);

        Assert.assertTrue(mode.playerStatus(player));
    }

    @After
    public void tearDown() {
        mode = null;
    }


}
