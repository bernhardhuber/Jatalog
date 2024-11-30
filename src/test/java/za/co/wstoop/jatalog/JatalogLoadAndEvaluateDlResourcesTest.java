package za.co.wstoop.jatalog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import za.co.wstoop.jatalog.output.OutputUtils;
import za.co.wstoop.jatalog.output.QueryOutput;
import za.co.wstoop.jatalog.statement.Statement;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author pi
 */
public class JatalogLoadAndEvaluateDlResourcesTest {

    @Test
    void testFamily1() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testFamily1");

        String name = "dl/family-1.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        // TODO test queries
        resultQueryOutput.output(System.out);

        assertAll(
                () -> assertEquals("married(alice, Spouse)?", resultQueryOutput.resultList.get(0).statement.toString()),
                () -> assertEquals("[{Spouse: adam}]", resultQueryOutput.resultList.get(0).answers.toString())
        );
    }

    @Test
    void testFamily2() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testFamily2");
        String name = "dl/family-2.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        // TODO test queries
        resultQueryOutput.output(System.out);

        assertAll(
                () -> assertEquals("family(X, Y)?", resultQueryOutput.resultList.get(0).statement.toString()),
                () -> assertEquals("[{X: carol, Y: dennis}, "
                        + "{X: dennis, Y: carol}, "
                        + "{X: carol, Y: david}, "
                        + "{X: david, Y: carol}, "
                        + "{X: bob, Y: david}, "
                        + "{X: bob, Y: dennis}, "
                        + "{X: david, Y: bob}, "
                        + "{X: dennis, Y: bob}, "
                        + "{X: alice, Y: dennis}, "
                        + "{X: dennis, Y: alice}, "
                        + "{X: alice, Y: david}, "
                        + "{X: david, Y: alice}, "
                        + "{X: bob, Y: carol}, "
                        + "{X: carol, Y: bob}, "
                        + "{X: alice, Y: carol}, "
                        + "{X: carol, Y: alice}, "
                        + "{X: alice, Y: bob}, "
                        + "{X: bob, Y: alice}, "
                        + "{X: alice, Y: bill}, {X: bill, Y: alice}]",
                        resultQueryOutput.resultList.get(0).answers.toString())
        );
    }

    @Test
    void testExpr() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testExpr");
        String name = "dl/expr.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        // TODO test queries
        resultQueryOutput.output(System.out);

        assertAll(
                () -> assertEquals("p(X), X <> Y, q(Y)?", resultQueryOutput.resultList.get(0).statement.toString()),
                () -> assertEquals("[{X: a, Y: c}, "
                        + "{X: b, Y: a}, "
                        + "{X: b, Y: c}]",
                        resultQueryOutput.resultList.get(0).answers.toString())
        );
    }

    @Test
    void testNum() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testNum");
        String name = "dl/num.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        // TODO test queries
        resultQueryOutput.output(System.out);

        assertAll(
                () -> assertEquals("num(N, V), V < 5?", resultQueryOutput.resultList.get(0).statement.toString()),
                () -> assertEquals("[{V: 1, N: a}, "
                        + "{V: 2, N: b}, "
                        + "{V: 3, N: c}, "
                        + "{V: 4, N: d}]",
                        resultQueryOutput.resultList.get(0).answers.toString())
        );
    }

    @Test
    void testSilbling() throws UnsupportedEncodingException, IOException, DatalogException {
        System.out.printf("\n----\nRunning testcase %s%n", "testSilbling");
        String name = "dl/silbling.dl";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(name);

        Jatalog jatalog = new Jatalog();
        ResultQueryOutput resultQueryOutput = new ResultQueryOutput();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            jatalog.executeAll(reader, resultQueryOutput);
        }

        // TODO test queries
        resultQueryOutput.output(System.out);

        assertAll(
                () -> assertEquals("sibling(A, B)?", resultQueryOutput.resultList.get(0).statement.toString()),
                () -> assertEquals("[{A: bob, B: bart}, "
                        + "{A: bart, B: bob}, "
                        + "{A: bob, B: betty}, "
                        + "{A: bart, B: betty}, "
                        + "{A: betty, B: bob}, "
                        + "{A: betty, B: bart}]",
                        resultQueryOutput.resultList.get(0).answers.toString())
        );
    }

    static class ResultQueryOutput implements QueryOutput {

        static class Pair {

            final Statement statement;
            final List<Map<String, String>> answers;

            Pair(Statement statement, List<Map<String, String>> answers) {
                this.statement = statement;
                this.answers = answers;
            }
        }

        final List<Pair> resultList = new ArrayList<>();

        public void output(PrintStream ps) {
            for (Pair p : resultList) {
                ps.printf("%s%n", p.statement.toString());

                if (p.answers.isEmpty()) {
                    ps.printf("No.%n");
                } else if (p.answers.size() == 1 && p.answers.get(0).isEmpty()) {
                    ps.printf("Yes.%n");
                } else {
                    p.answers.forEach(m -> {
                        ps.printf("%s%n", OutputUtils.bindingsToString(m));
                    });
                }
            }
        }

        @Override
        public void writeResult(Statement statement, Collection<Map<String, String>> answers) {
            if (answers.isEmpty()) {
                this.resultList.add(
                        new Pair(statement, Collections.emptyList()
                        ));
            } else if (answers.iterator().next().isEmpty()) {
                Map<String, String> m = Collections.emptyMap();
                this.resultList.add(
                        new Pair(statement, Collections.singletonList(m)));
            } else {
                this.resultList.add(
                        new Pair(statement, (List) answers)
                );
            }

        }
    }
}
