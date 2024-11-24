package za.co.wstoop.jatalog.engine;

import java.util.Map;
import org.junit.jupiter.api.Test;
import za.co.wstoop.jatalog.DatalogException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * I'm not too concerned to get 100% coverage for StackMap because it is
 * basically just the get(), put() and containsKey() methods that are used
 * by Jatalog
 */
public class StackMapTest {

    @Test
    public void testBase() throws DatalogException {
        Map<String, String> parent = new StackMap<>();
        StackMap<String, String> child = new StackMap<>(parent);

        assertTrue(child.isEmpty());

        parent.put("W", "0");
        parent.put("X", "1");
        assertFalse(child.isEmpty());

        child.put("Y", "2");
        assertTrue(child.get("X").equals("1"));
        assertTrue(child.get("Y").equals("2"));
        assertTrue(child.get("Z") == null);

        assertTrue(child.containsKey("X"));
        assertTrue(child.containsKey("Y"));
        assertFalse(child.containsKey("Z"));

        assertTrue(child.containsValue("1"));
        assertTrue(child.containsValue("2"));
        assertFalse(child.containsValue("3"));

        child.put("X", "5");
        assertTrue(child.get("X").equals("5"));
        assertTrue(parent.get("X").equals("1"));
        assertTrue(child.containsValue("5"));
        assertFalse(parent.containsValue("5"));

        assertTrue(child.size() == 3);

        assertTrue(child.toString().contains("X: 5"));
        assertTrue(child.toString().contains("Y: 2"));
        assertTrue(child.toString().contains("W: 0"));

        Map<String, String> flat = child.flatten();
        assertTrue(flat.get("W").equals("0"));
        assertTrue(flat.get("X").equals("5"));
        assertTrue(flat.get("Y").equals("2"));
        assertTrue(flat.get("Z") == null);

        child.clear();
        assertTrue(parent.get("X").equals("1"));
        assertTrue(child.size() == 0);
        assertTrue(child.isEmpty());
        assertTrue(child.get("X") == null);
    }

    @Test
    public void testEqualsAndHashCode() {
        StackMap<String, String> sm1 = new StackMap<>();
        StackMap<String, String> sm2 = new StackMap<>();

        assertAll(
                () -> {
                    assertEquals(sm1.hashCode(), sm2.hashCode());
                    assertTrue(sm1.equals(sm2));
                },
                () -> {
                    sm1.put("key1", "value1");
                    sm2.put("key1", "value1");
                    assertEquals(sm1.hashCode(), sm2.hashCode());
                    assertTrue(sm1.equals(sm2));
                },
                () -> {
                    sm1.put("key2", "value21");
                    sm2.put("key2", "value22");
                    assertNotEquals(sm1.hashCode(), sm2.hashCode());
                    assertFalse(sm1.equals(sm2));
                }
        );
    }

    @Test
    public void testKeySetAndValuesAndEntrySet() {
        StackMap<String, String> sm1 = new StackMap<>();

        assertAll(
                () -> {
                    assertEquals("[]", sm1.keySet().toString());
                    assertEquals("[]", sm1.values().toString());
                    assertEquals("[]", sm1.entrySet().toString());
                },
                () -> {
                    sm1.put("key1", "value1");
                    assertEquals("[key1]", sm1.keySet().toString());
                    assertEquals("[value1]", sm1.values().toString());
                    assertEquals("[key1=value1]", sm1.entrySet().toString());
                },
                () -> {
                    sm1.put("key2", "value2");
                    assertEquals("[key1, key2]", sm1.keySet().toString());
                    assertEquals("[value1, value2]", sm1.values().toString());
                    assertEquals("[key1=value1, key2=value2]", sm1.entrySet().toString());
                }
        );
    }

}
