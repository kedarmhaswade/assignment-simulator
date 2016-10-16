package org.kedar.pra;

import com.google.common.base.Charsets;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.*;
import static org.kedar.pra.Simulator.processInput;

/**
 * Created by kedar on 10/15/16.
 */
public class SimulatorTest {
    @Test
    public void processInputTest() throws Exception {
        InputStream is = new ByteArrayInputStream("2\n5\n1 5 85 4\n2 2 90 3\n3 3 80 -2\n4 2 88 4\n5 4 75 -3".getBytes(Charsets.UTF_8));
        Object[] input = processInput(is);
        int ticks = (Integer)input[0];
        Set<Learner> learners = (Set) input[1];
        assertEquals(2L, ticks);
        assertEquals(5L, learners.size());
    }

}