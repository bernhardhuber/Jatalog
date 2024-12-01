package za.co.wstoop.jatalog.output;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Jatalog;
import za.co.wstoop.jatalog.output.ResultQueryOutput.PairStatementAndAnswers;
import za.co.wstoop.jatalog.statement.Statement;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author pi
 */
public class ResultQueryOutputTest {

    ResultQueryOutput instance;

    @BeforeEach
    void setup() {
        instance = new ResultQueryOutput();
    }

    @Test
    void testWriteResultNo() throws DatalogException {
        List<Map<String, String>> answerList = Collections.emptyList();
        Statement statement = (Jatalog datalog, Map<String, String> bindings) -> answerList;
        Jatalog datalog = null;
        Collection<Map<String, String>> answers = statement.execute(datalog);
        instance.writeResult(statement, answers);
        PairStatementAndAnswers result = instance.getResult();
        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getStatement()),
                () -> assertEquals(0, result.getAnswers().size())
        );
    }

    @Test
    void testWriteResultYes() throws DatalogException {
        List<Map<String, String>> answerList = Arrays.asList(Collections.emptyMap());
        Statement statement = (Jatalog datalog, Map<String, String> bindings) -> answerList;
        Jatalog datalog = null;
        Collection<Map<String, String>> answers = statement.execute(datalog);
        instance.writeResult(statement, answers);
        PairStatementAndAnswers result = instance.getResult();
        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getStatement()),
                () -> assertEquals(1, result.getAnswers().size()),
                () -> assertEquals(0, result.getAnswers().get(0).size())
        );
    }

    @ParameterizedTest
    @MethodSource
    void testWriteResult(List<Map<String, String>> answerList) throws DatalogException {
        Statement statement = (Jatalog datalog, Map<String, String> bindings) -> answerList;
        Jatalog datalog = null;
        Collection<Map<String, String>> answers = statement.execute(datalog);
        instance.writeResult(statement, answers);
        PairStatementAndAnswers result = instance.getResult();
        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getStatement()),
                () -> assertInRange(1, 2, result.getAnswers().size()),
                () -> assertInRange(1, 2, result.getAnswers().get(0).size())
        );

    }

    static void assertInRange(int min, int max, int value) {
        String m = String.format("min: %d, max: %d, value: %d", min, max, value);
        assertTrue(min <= value, m);
        assertTrue(value <= max, m);
    }

    static Stream<List<Map<String, String>>> testWriteResult() {
        return Stream.of(
                Arrays.asList(new MapBuilder<String, String>()
                        .put("k1", "v1")
                        .build()),
                Arrays.asList(new MapBuilder<String, String>()
                        .put("k11", "v11")
                        .put("k12", "v12")
                        .build()),
                Arrays.asList(
                        new MapBuilder<String, String>()
                                .put("k1", "v1")
                                .build(),
                        new MapBuilder<String, String>()
                                .put("k2", "v2")
                                .build()
                ),
                Arrays.asList(
                        new MapBuilder<String, String>()
                                .put("k11", "v11")
                                .put("k12", "v12")
                                .build(),
                        new MapBuilder<String, String>()
                                .put("k21", "v21")
                                .put("k22", "v22")
                                .build()
                )
        );
    }
}
