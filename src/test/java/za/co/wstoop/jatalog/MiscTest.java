package za.co.wstoop.jatalog;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author pi
 */
public class MiscTest {

    @ParameterizedTest
    @ValueSource(strings = {"=", "!=", "<>", "<", "<=", ">", ">="})
    void testArraysAsListWithStringArray(String expectAssertTrue) {
        List<String> l1 = Arrays.asList(new String[]{"=", "!=", "<>", "<", "<=", ">", ">="});
        assertTrue(l1.contains(expectAssertTrue));
        assertFalse(l1.contains("abc"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"=", "!=", "<>", "<", "<=", ">", ">="})
    void testArraysAsListWithString(String expectAssertTrue) {
        List<String> l2 = Arrays.asList("=", "!=", "<>", "<", "<=", ">", ">=");
        assertTrue(l2.contains(expectAssertTrue));
        assertFalse(l2.contains("abc"));
    }
}
