package za.co.wstoop.jatalog.statement;

import org.junit.Test;
import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Expr;
import za.co.wstoop.jatalog.Jatalog;
import za.co.wstoop.jatalog.TestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Simple tests of  {@link InsertFactStatement}.
 *
 * @author berni3
 */
public class InsertFactStatementTest {
    @Test
    public void given_newFact_then_insertFactStatement_succeeds() throws DatalogException {
        Jatalog jatalog = TestUtils.createDatabase();
        InsertFactStatement insertFactStatement = new InsertFactStatement(Expr.expr("newFact", "a", "b"));
        insertFactStatement.execute(jatalog);

        String m = "" + jatalog.getEdbProvider().allFacts();
        assertEquals(m, 1L,
                jatalog.getEdbProvider().allFacts().stream()
                        .map(Expr::getPredicate)
                        .filter("newFact"::equals)
                        .count());

    }
}