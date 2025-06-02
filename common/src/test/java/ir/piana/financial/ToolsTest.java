package ir.piana.financial;

import ir.piana.financial.solutions.common.tools.Stringer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class ToolsTest {
    @Test
    void theStringer_Concat() {
        Assertions.assertEquals("abc", Stringer.concat("a", "b", "c"));
        Assertions.assertEquals("", Stringer.concat());
    }
}
