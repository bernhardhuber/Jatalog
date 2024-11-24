package za.co.wstoop.jatalog.engine;

import org.junit.Test;
import za.co.wstoop.jatalog.DatalogException;
import za.co.wstoop.jatalog.Expr;
import za.co.wstoop.jatalog.Rule;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Simple tests of static methods of {@link Engine}.
 *
 * @author berni3
 */
public class EngineTest {

    public static class EngineReorderQueryTest {
        @Test
        public void given_1_expression_then_reorderQuery_succeeds() {
            List<Expr> query = Collections.singletonList(Expr.expr("pred", "A", "B"));
            List<Expr> orderedQuery = Engine.reorderQuery(query);
            assertEquals(1, orderedQuery.size());
            assertEquals("pred", orderedQuery.get(0).getPredicate());
        }

        @Test
        public void given_2_expressions_with_not_then_reorderQuery_succeeds() {
            List<Expr> query = Arrays.asList(
                    Expr.not("pred1", "a, B"),
                    Expr.expr("pred2", "c", "D")
            );
            List<Expr> orderedQuery = Engine.reorderQuery(query);
            assertEquals(2, orderedQuery.size());
            assertEquals("pred2", orderedQuery.get(0).getPredicate());
            assertEquals("pred1", orderedQuery.get(1).getPredicate());
        }
    }

    public static class EngineComputeStratificationTest {
        @Test
        public void given_rules_wo_not_then_computeStratification_succeeds() throws DatalogException {
            List<Rule> rules = Arrays.asList(
                    new Rule(Expr.expr("head1", "A", "B"),
                            Collections.singletonList(Expr.expr("pred1", "A", "B"))
                    ),
                    new Rule(Expr.expr("head2", "A", "B"),
                            Collections.singletonList(Expr.expr("pred2", "A", "B"))
                    ),
                    new Rule(Expr.expr("head3", "A", "B"),
                            Arrays.asList(
                                    Expr.expr("pred31", "A", "B"),
                                    Expr.expr("pred32", "A", "B"))
                    ),
                    new Rule(Expr.expr("head4", "A", "B"),
                            Arrays.asList(
                                    Expr.expr("head1", "A", "B"),
                                    Expr.expr("head2", "A", "B"))
                    )
            );
            List<Collection<Rule>> strata = Engine.computeStratification(rules);
            assertEquals(2, strata.size());
            assertEquals("[head1(A, B) :- pred1(A, B), head2(A, B) :- pred2(A, B), head3(A, B) :- pred31(A, B), pred32(A, B), head4(A, B) :- head1(A, B), head2(A, B)]", strata.get(0).toString());
            assertEquals("[head1(A, B) :- pred1(A, B), head2(A, B) :- pred2(A, B), head3(A, B) :- pred31(A, B), pred32(A, B), head4(A, B) :- head1(A, B), head2(A, B)]", strata.get(1).toString());
        }
    }

    public static class EngineBuildDependentRulesTest {
        @Test
        public void given_rules_then_buildDependentRules_succeeds() {
            List<Rule> rules = Arrays.asList(
                    new Rule(Expr.expr("head1", "A", "B"),
                            Collections.singletonList(Expr.expr("pred1", "A", "B"))
                    ),
                    new Rule(Expr.expr("head2", "A", "B"),
                            Collections.singletonList(Expr.expr("pred2", "A", "B"))
                    ),
                    new Rule(Expr.expr("head3", "A", "B"),
                            Arrays.asList(
                                    Expr.expr("pred31", "A", "B"),
                                    Expr.expr("pred32", "A", "B"))
                    ),
                    new Rule(Expr.expr("head4", "A", "B"),
                            Arrays.asList(
                                    Expr.expr("head1", "A", "B"),
                                    Expr.expr("head2", "A", "B"))
                    )
            );

            Map<String, Collection<Rule>> map = Engine.buildDependentRules(rules);
            assertEquals("[head1(A, B) :- pred1(A, B)]", map.get("pred1").toString());
            assertEquals("[head2(A, B) :- pred2(A, B)]", map.get("pred2").toString());
            assertEquals("[head3(A, B) :- pred31(A, B), pred32(A, B)]", map.get("pred31").toString());
            assertEquals("[head3(A, B) :- pred31(A, B), pred32(A, B)]", map.get("pred32").toString());
            assertEquals("[head4(A, B) :- head1(A, B), head2(A, B)]", map.get("head1").toString());
            assertEquals("[head4(A, B) :- head1(A, B), head2(A, B)]", map.get("head2").toString());
        }
    }
}