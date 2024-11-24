package za.co.wstoop.jatalog.shell2;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author pi
 */
public class HistoryOfCommandsTest {

    HistoryOfCommands instance;

    @BeforeEach
    void setUp() {
        instance = new HistoryOfCommands();
    }

    /**
     * Test of addToHistory method, of class HistoryOfCommands.
     */
    @Test
    public void testAddToHistory() {
        assertEquals(1, instance.addToHistory("a b c"));
        assertEquals(2, instance.addToHistory("ABC"));
    }

    /**
     * Test of retrieveFromHistory method, of class HistoryOfCommands.
     */
    @Test
    public void testRetrieveFromHistory() {
        instance.addToHistory("a b c");
        instance.addToHistory("ABC");
        assertEquals("a b c", instance.retrieveFromHistory(0).get());
        assertEquals("ABC", instance.retrieveFromHistory(1).get());
    }

    /**
     * Test of retrieveAllHistoryEntries method, of class HistoryOfCommands.
     */
    @Test
    public void testRetrieveAllHistoryEntries() {
        instance.addToHistory("a b c");
        instance.addToHistory("ABC");
        List<String> result = instance.retrieveAllHistoryEntries();
        assertEquals("a b c", result.get(0));
        assertEquals("ABC", result.get(1));
    }

}
