package za.co.wstoop.jatalog.output;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author pi
 */
public class OutputUtilsTest {

    /**
     * Test of listToString method, of class OutputUtils.
     */
    @Test
    public void testListToString() {
        List collection = Arrays.asList("a", 1, "b");
        assertEquals("[a. 1. b. ]", OutputUtils.listToString(collection));
    }

    /**
     * Test of bindingsToString method, of class OutputUtils.
     */
    @Test
    public void testBindingsToString() {
        assertAll(
                () -> {
                    Map<String, String> bindings = new MapBuilder<String, String>()
                            .build();
                    assertEquals("{}", OutputUtils.bindingsToString(bindings));
                }, () -> {
                    Map<String, String> bindings = new MapBuilder<String, String>()
                            .put("k1", "v1")
                            .build();
                    assertEquals("{k1: v1}", OutputUtils.bindingsToString(bindings));
                },
                () -> {
                    Map<String, String> bindings = new MapBuilder<String, String>()
                            .put("k1", "\"a\"")
                            .build();
                    assertEquals("{k1: \"a\\\"\"}", OutputUtils.bindingsToString(bindings));
                }
        );

    }

    /**
     * Test of answersToString method, of class OutputUtils.
     */
    @Test
    public void testAnswersToString() {
        assertAll(
                () -> {
                    Collection<Map<String, String>> answers = Collections.emptyList();
                    assertEquals("No.", OutputUtils.answersToString(answers));
                },
                () -> {
                    Collection<Map<String, String>> answers = Arrays.asList(
                            new MapBuilder<String, String>().build()
                    );
                    assertEquals("Yes.", OutputUtils.answersToString(answers));
                },
                () -> {
                    Collection<Map<String, String>> answers = Arrays.asList(
                            new MapBuilder<String, String>().put("k1", "v1").build()
                    );
                    assertEquals("{k1: v1}", OutputUtils.answersToString(answers));
                }
        );
    }

    static class MapBuilder<K, V> {

        Map<K, V> m;

        MapBuilder() {
            this.m = new HashMap<>();
        }

        MapBuilder<K, V> put(K k, V v) {
            this.m.put(k, v);
            return this;
        }

        Map<K, V> build() {
            return this.m;
        }
    }
}
