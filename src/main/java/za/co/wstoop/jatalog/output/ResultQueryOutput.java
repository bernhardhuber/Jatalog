package za.co.wstoop.jatalog.output;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import za.co.wstoop.jatalog.output.OutputUtils;
import za.co.wstoop.jatalog.output.QueryOutput;
import za.co.wstoop.jatalog.statement.Statement;

/**
 *
 * @author pi
 */
public class ResultQueryOutput implements QueryOutput {

    private static final String PREFIX = "";
    private static final String PREFIX_NO = PREFIX + "No.";
    private static final String PREFIX_YES = PREFIX + "Yes.";

    public static class PairStatementAndAnswers {

        private final Statement statement;
        private final List<Map<String, String>> answers;

        PairStatementAndAnswers(Statement statement, List<Map<String, String>> answers) {
            this.statement = statement;
            this.answers = answers;
        }

        public Statement getStatement() {
            return statement;
        }

        public List<Map<String, String>> getAnswers() {
            return answers;
        }

    }
    private PairStatementAndAnswers result;

    public PairStatementAndAnswers getResult() {
        return result;
    }

    public void output(PrintStream ps) {
        PairStatementAndAnswers p = result;
        ps.printf("%s%n", p.statement.toString());
        if (p.answers.isEmpty()) {
            ps.printf("%s%n", PREFIX_NO);
        } else if (p.answers.size() == 1 && p.answers.get(0).isEmpty()) {
            ps.printf("%s%n", PREFIX_YES);
        } else {
            p.answers.forEach(m -> {
                ps.printf("%s%s%n", PREFIX, OutputUtils.bindingsToString(m));
            });
        }
    }

    @Override
    public void writeResult(Statement statement, Collection<Map<String, String>> answers) {
        if (answers.isEmpty()) {
            result = new PairStatementAndAnswers(statement, Collections.emptyList());
        } else if (answers.iterator().next().isEmpty()) {
            Map<String, String> m = Collections.emptyMap();
            result = new PairStatementAndAnswers(statement, Collections.singletonList(m));
        } else {
            result = new PairStatementAndAnswers(statement, (List) answers);
        }
    }

}
