package org.kedar.pra;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kedar on 15/10/16.
 */
public class TimeTickTest {
    @Test
    public void valueOfTest() throws Exception {
        long beginningOfTime = System.currentTimeMillis();
        assertTrue(TimeTick.since(beginningOfTime).getValue() >= 0);
    }

    @Test
    public void compareTicks() {
        TimeTick t1 = new TimeTick(1);
        TimeTick t2 = new TimeTick(10);
        TimeTick t1Copy = new TimeTick(1);

        assertTrue(t1.compareTo(t2) < 0);
        assertTrue(t1.equals(t1Copy));
    }

}