package board;

import com.raczy.chinesecheckers.builder.BoardBuilder;
import com.raczy.chinesecheckers.Field;
import com.raczy.chinesecheckers.builder.StandardBoardBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Created by kacperraczy on 14.12.2017.
 */
public class EdgeLR_BoardTest extends BoardTest{

    @Before
    public void initialize() {
        BoardBuilder builder = new StandardBoardBuilder();
        builder.generateMainBoardPart();

        builder.generatePlayerBoardPart(0);
        builder.generatePlayerBoardPart(3);

        this.board = builder.getResult();
    }

    @Test
    public void test_Left_RightEdge() {
        Map<Integer, Field> firstPlayerZone = this.board.getPlayerZones().get(0);
        Assert.assertEquals(10, firstPlayerZone.size());

        int max = 0;
        for(Integer i: firstPlayerZone.keySet()) {
            if (i>max) {
                max = i;
            }
        }

        testEdges(max, Field.RIGHT);
    }
}
