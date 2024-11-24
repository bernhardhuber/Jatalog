package za.co.wstoop.jatalog.shell2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author pi
 */
public class ShellCommandsTest {

    @ParameterizedTest
    @ValueSource(strings = {"a b c", "a  b   c", "a\rb\nc\r\n", "a\tb\tc\t"})
    public void testTokenizeTheLineAbc(String abc) {
        assertArrayEquals(new String[]{"a", "b", "c"}, ShellCommands.tokenizeTheLine(abc));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a b c", "a  b   c", "a\rb\nc\r\n", "a\tb\tc\t"})
    public void testTokenizeArgsOnlyAbc(String abc) {
        assertArrayEquals(new String[]{"b", "c"}, ShellCommands.tokenizeArgsOnly(abc));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a b c", "a  b   c", "a\rb\nc\r\n", "a\tb\tc\t"})
    public void testExtractAllArgsAbc(String abc) {
        assertEquals("a", ShellCommands.extractAllArgs(abc));
    }

}
