package game;

import com.raczy.chinesecheckers.*;
import com.raczy.chinesecheckers.exceptions.GameException;
import com.raczy.chinesecheckers.session.GameSession;
import com.raczy.chinesecheckers.session.GameSessionCallback;
import com.raczy.chinesecheckers.session.GameSessionState;
import com.raczy.chinesecheckers.session.StandardGameSession;
import com.sun.xml.internal.ws.policy.AssertionSet;
import org.junit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by kacperraczy on 22.12.2017.
 */
public class GameSessionTest {

    private final int testedPlayerNo = 4;
    private GameSession session;
    private ArrayList<Player> players;

    @Before
    public void initialize() {
        GameSession s = new StandardGameSession(testedPlayerNo);

        Player temp;
        for(int i = 0; i < testedPlayerNo; i++) {
            temp = new Player(i,"Player" + i);
            s.addPlayer(temp);
        }

        this.session = s;
        this.players = s.getPlayers();
    }

    @Test
    public void test_SessionState() {
        GameSessionState state = this.session.getState();
        Assert.assertEquals(GameSessionState.IN_PROGRESS, state);
    }

    @Test
    public void test_ZoneIds() {
        ArrayList<Integer> ids = new ArrayList<>(players.size());

        for(Player p: players) {
            ids.add(p.getZoneID());
        }

        int zone, oppoZone;
        boolean actual;
        for(Player p: players) {
            zone = p.getZoneID();
            oppoZone = Field.oppositeEdge(zone);

            actual = ids.contains(oppoZone);
            Assert.assertTrue(actual);
        }
    }

    @Test
    public void test_stateDone_playersRemoved() {
        ArrayList<Player> playersCopy = new ArrayList<>(players);
        for(Player p : playersCopy) {
            session.removePlayer(p);
        }

        Assert.assertEquals(GameSessionState.DONE, session.getState());
    }


    @Test
    public void test_BoardUpdate() {
        Field[] fieldArr = getPossibleMove();
        final Field start = fieldArr[0];
        final Field destination = fieldArr[1];


        Assert.assertNotNull(start);
        Assert.assertNotNull(destination);

        GameInfo info = new GameInfo(new Date(), start.getId(), destination.getId());
        session.performMove(info, new GameSessionCallback() {
            @Override
            public void onAccept(Boolean won, GameInfo gameInfo) {
                int playerId = session.getCurrentPlayer().getId();
                Assert.assertEquals(playerId, destination.getChecker().getId());
            }

            @Override
            public void onError(GameException e) {
                Assert.assertTrue(false);
            }
        });

    }

    public Field[] getPossibleMove() {
        Field[] fieldArr = new Field[2];

        Board board = session.getBoard();
        int zoneId = session.getCurrentPlayer().getZoneID();
        Map<Integer, Field> map = board.getPlayerZones().get(zoneId);

        Field temp = null;
        Field temp2 = null;
        for(Field f: map.values()) {
            for(Field n: f.getNeighbours()) {
                if (n != null && n.getChecker() == null) {
                    temp = f;
                    temp2 = n;
                    break;
                }
            }

            if(temp != null) {
                break;
            }
        }

        fieldArr[0] = temp;
        fieldArr[1] = temp2;


        return fieldArr;
    }


    @Test
    public void test_Turns() {
        Field[] fieldArr = getPossibleMove();
        Player prevPlayer = session.getCurrentPlayer();

        final Field start = fieldArr[0];
        final Field destination = fieldArr[1];


        Assert.assertNotNull(start);
        Assert.assertNotNull(destination);

        GameInfo info = new GameInfo(new Date(), start.getId(), destination.getId());
        session.performMove(info, new GameSessionCallback() {
            @Override
            public void onAccept(Boolean won, GameInfo gameInfo) {}

            @Override
            public void onError(GameException e) {
                Assert.assertTrue(false);
            }
        });

        Assert.assertNotEquals(prevPlayer.getId(), session.getCurrentPlayer().getId());

    }


    @After
    public void tearDown() {
        this.session = null;
        this.players = null;
    }
}
