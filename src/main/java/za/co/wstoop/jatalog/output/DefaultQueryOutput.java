package za.co.wstoop.jatalog.output;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

import za.co.wstoop.jatalog.statement.Statement;

/**
 * Default implementation of {@link QueryOutput} that uses {@code System.out}.
 */
public class DefaultQueryOutput implements QueryOutput {

    private static final String PREFIX = "  ";
    private static final String PREFIX_NO = PREFIX + "No.";
    private static final String PREFIX_YES = PREFIX + "Yes.";

    final PrintStream ps;

    /**
     * Use {@code System.out}.
     */
    public DefaultQueryOutput() {
        ps = System.out;
    }

    /**
     * Use provided {@code PrintStream}.
     *
     * @param ps
     */
    public DefaultQueryOutput(PrintStream ps) {
        this.ps = ps;
    }

    @Override
    public void writeResult(Statement statement, Collection<Map<String, String>> answers) {
        ps.println(statement.toString());
        if (answers.isEmpty()) {
            ps.println(PREFIX_NO);
        } else if (answers.iterator().next().isEmpty()) {
            ps.println(PREFIX_YES);
        } else {
            for (Map<String, String> answer : answers) {
                ps.println(PREFIX + OutputUtils.bindingsToString(answer));
            }
        }
    }
}
