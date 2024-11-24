package za.co.wstoop.jatalog.statement;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Expr;
import za.co.wstoop.jatalog.Jatalog;
import za.co.wstoop.jatalog.Rule;
import za.co.wstoop.jatalog.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Simple tests of {@link InsertRuleStatement}.
 *
 * @author berni3
 */
public class InsertRuleStatementTest {

    @Test
    public void given_newRule_then_insertRuleStatement_succeeds() throws DatalogException {
        Jatalog jatalog = TestUtils.createDatabase();
        Rule newRule = new Rule(Expr.expr("newPredicate", "A"),
                Collections.singletonList(Expr.expr("parent", "a", "A"))
        );
        InsertRuleStatement insertRuleStatement = new InsertRuleStatement(newRule);
        insertRuleStatement.execute(jatalog);

        String m = "" + jatalog.getIdb();
        assertEquals(1L,
                jatalog.getIdb().stream()
                        .map(Rule::getHead)
                        .map(Expr::getPredicate)
                        .filter("newPredicate"::equals)
                        .count(),
                m);
    }
}
