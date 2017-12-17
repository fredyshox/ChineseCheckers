package board;

import com.raczy.chinesecheckers.Board;
import com.raczy.chinesecheckers.Field;
import org.junit.*;

/**
 * Created by kacperraczy on 12.12.2017.
 */
public abstract class BoardTest {

    protected Board board;

    protected void testEdges(int startIdx, int testedEdge) {
        Field starting = null, temp;
        temp = this.board.getFieldMap().get(startIdx);
        int nIter, i;

        System.out.println("First iteration");
        int rowDownEdge = (testedEdge + 2) % 6;
        for(nIter = 1; nIter < 10; nIter++) {
            Assert.assertNotNull(temp);

            starting = temp;
            testRow(temp, nIter, testedEdge);

            temp = temp.getNeighbours()[rowDownEdge];
        }

        System.out.println("Second iteration");
        rowDownEdge = (testedEdge + 1) % 6;
        temp = starting.getNeighbours()[rowDownEdge];

        for(nIter = nIter - 2; nIter > 0; nIter--) {
            Assert.assertNotNull(temp);
            testRow(temp, nIter, testedEdge);

            temp = temp.getNeighbours()[rowDownEdge];
        }

    }

    protected void testRow(Field start, int nIter, int testedEdge) {
        Field temp = start;
        Field temp2 = temp;
        int i = 1;
        while ((temp = temp.getNeighbours()[testedEdge]) != null) {
            i++;

            //checks if opposite edge references prev object
            Assert.assertEquals(temp2, temp.getNeighbours()[Field.oppositeEdge(testedEdge)]);
            temp2 = temp;
        }

        Assert.assertEquals(nIter, i);
    }

    protected int getBoardIdx(int rowCount) {
        if(rowCount <= 5) {
            return 0;
        }else {
            return (rowCount - 1) + getBoardIdx(rowCount -1);
        }
    }


    @After
    public void tearDown() {
        this.board = null;
    }
}
