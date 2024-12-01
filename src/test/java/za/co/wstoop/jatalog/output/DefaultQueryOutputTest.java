package za.co.wstoop.jatalog.output;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Jatalog;
import za.co.wstoop.jatalog.statement.Statement;

/**
 *
 * @author pi
 */
public class DefaultQueryOutputTest {

    DefaultQueryOutput instance;

    @BeforeEach
    void setup() {
        instance = new DefaultQueryOutput();
    }

    @ParameterizedTest
    @MethodSource
    void testWriteResult(List<Map<String, String>> answerList) throws DatalogException {
        Statement statement = (Jatalog datalog, Map<String, String> bindings) -> answerList;
        Jatalog datalog = null;
        Collection<Map<String, String>> answers = statement.execute(datalog);
        instance.writeResult(statement, answers);
    }

    static Stream<List<Map<String, String>>> testWriteResult() {
        return Stream.of(Collections.emptyList(),
                Arrays.asList(Collections.emptyMap()),
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
