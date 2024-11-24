package za.co.wstoop.jatalog.statement;

import org.junit.Test;
import za.co.wstoop.jatalog.*;

import java.util.Collections;
import static org.junit.Assert.assertEquals;

/**
 * Simple tests of  {@link InsertRuleStatement}.
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
        assertEquals(m, 1L,
                jatalog.getIdb().stream()
                        .map(Rule::getHead)
                        .map(Expr::getPredicate)
                        .filter("newPredicate"::equals)
                        .count());
    }
}