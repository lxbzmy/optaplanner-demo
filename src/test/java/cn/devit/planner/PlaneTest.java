package cn.devit.planner;

import static org.junit.Assert.*;

import org.junit.Test;

public class PlaneTest {

    @Test
    public void should_equals() throws Exception {
        assertEquals(new Plane("1"), new Plane("1"));
    }
}
