package za.co.wstoop.jatalog;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import za.co.wstoop.jatalog.Parser.DoubleParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author pi
 */
public class DoubleParserTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "X", "!", "#"})
    void testTryParseDoubleFalse(String str) {
        assertFalse(DoubleParser.tryParseDouble("a"), "" + str);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.0", "1", "100", "-1"})
    void testTryParseDoubleTrue(String str) {
        assertTrue(DoubleParser.tryParseDouble(str), "" + str);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "X", "!", "#"})
    void testParseDoubleFalse(String str) {
        assertEquals(0.0, DoubleParser.parseDouble("a"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.0", "1", "100", "-1"})
    void testParseDoubleValue(String str) {
        double expectedVal = Double.parseDouble(str);
        assertEquals(expectedVal, DoubleParser.parseDouble(str));
    }
}
