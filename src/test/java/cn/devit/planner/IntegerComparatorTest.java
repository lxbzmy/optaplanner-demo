package cn.devit.planner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class IntegerComparatorTest {

    @Test
    public void abc() throws Exception {
        int result = new IntegerComparator().compare(1, 2);
        assertThat(result, is(-1));
    }
}
