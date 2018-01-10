package server;

import com.google.gson.Gson;
import com.raczy.chinesecheckers.GameInfo;
import com.raczy.server.Utility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by kacperraczy on 03.01.2018.
 */
public class SerializationTest {

    private Gson gson;

    @Before
    public void init() {
        gson = Utility.getGson();
    }


    /**
     * Testing gson object build in Utility class
     */
    @Test
    public void testDateString() {
        String expected = "{\"createdAt\":\"2018-01-03T20:32:09.246Z\",\"oldFieldID\":94,\"newFieldID\":56}";
        GameInfo info = gson.fromJson(expected, GameInfo.class);
        String actual = gson.toJson(info);

        Assert.assertEquals(expected, actual);
    }


}
