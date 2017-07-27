package cn.devit.planner;

import static org.junit.Assert.*;

import org.junit.Test;

public class LegTest {

    @Test
    public void to_leg_equals() throws Exception {
        assertEquals(new Leg("1","2"), new Leg("1","2"));
    }
    
}
