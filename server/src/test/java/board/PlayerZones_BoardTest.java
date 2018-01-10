package board;

import com.raczy.chinesecheckers.Field;
import com.raczy.chinesecheckers.Player;
import com.raczy.chinesecheckers.builder.BoardBuilder;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by kacperraczy on 10.01.2018.
 */
public class PlayerZones_BoardTest extends BoardTest {

    private int testedSide = 0;
    private Player player;

    @Before
    public void initialize() {
        BoardBuilder builder = new StandardBoardBuilder();
        builder.generateMainBoardPart();
        builder.generatePlayerBoardPart(testedSide);
        board = builder.getResult();

        player = new Player(0,"");
    }

    @Test
    public void test_fillPlayerZone() {
        player.setZoneID(testedSide);
        board.fillZone(testedSide, player);

        for(Field f: board.getPlayerZones().get(testedSide).values()) {
            Assert.assertSame(player, f.getChecker());
        }
    }

}