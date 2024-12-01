package za.co.wstoop.jatalog;

import java.io.StreamTokenizer;
import java.io.StringReader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import za.co.wstoop.jatalog.StreamTokenizerBuilder;
import za.co.wstoop.jatalog.statement.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author pi
 */
public class ParserTest {

    /**
     * Test of parseStmt method, of class Parser.
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "pred(a).",
        "pred ( a ) .",
        "pred(a1,a2).",
        "pred(a,b).",
        "pred ( a , b ) .",
        " pred ( a , b , c ) ."
    })
    public void testParseStmtInsertFactStatement(String s) throws DatalogException {
        try (StringReader sr = new StringReader(s)) {
            StreamTokenizer scan = new StreamTokenizerBuilder(sr).build();

            Statement result = Parser.parseStmt(scan);
            assertNotNull(result);
            assertEquals("za.co.wstoop.jatalog.statement.InsertFactStatement", result.getClass().getName());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "pred(X):-a(X).",
        "pred ( X ) :- a(X) .",
        " pred(X) :- a ( X ) , b ( X ) ."
    })
    public void testParseStmtInsertRuleStatement(String s) throws DatalogException {
        try (StringReader sr = new StringReader(s)) {
            StreamTokenizer scan = new StreamTokenizerBuilder(sr).build();

            Statement result = Parser.parseStmt(scan);
            assertNotNull(result);
            assertEquals("za.co.wstoop.jatalog.statement.InsertRuleStatement", result.getClass().getName());
        }
    }

    /**
     * Test of parseExpr method, of class Parser.
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "x = x",
        "X = x",
        "X = X",
        "X = Y",
        "x <> x",
        "x < x",
        "x <= x",
        "x > x",
        "x >= x",})
    public void testParseExpr(String s) throws DatalogException {
        try (StringReader sr = new StringReader(s)) {
            StreamTokenizer scan = new StreamTokenizerBuilder(sr).build();

            Expr result = Parser.parseExpr(scan);
            assertNotNull(result);
            assertEquals(s, result.toString());
        }

    }
}
