package za.co.wstoop.jatalog;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import za.co.wstoop.jatalog.Expr.ExprException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExprTest {

    @Test
    public void testEquals() {
        final Expr e1 = new Expr("foo", "a", "b");
        assertTrue(e1.getPredicate().equals("foo"));
        assertTrue(e1.arity() == 2);
        assertFalse(e1.isNegated());

        final Expr e2 = new Expr("foo", "a", "b");
        assertTrue(e1.equals(e2));

        final Expr e3 = new Expr("bar", "a", "b");
        assertFalse(e1.equals(e3));
        final Expr e4 = new Expr("foo", "a", "b", "c");
        assertTrue(e4.arity() == 3);
        assertFalse(e1.equals(e4));

        assertFalse(e1.equals(null));
        assertFalse(e1.equals(this));
    }

    @Test
    public void testGround() {
        assertTrue(Jatalog.isVariable("X"));
        assertFalse(Jatalog.isVariable("x"));
        assertTrue(Jatalog.isVariable("Hello"));
        assertFalse(Jatalog.isVariable("hello"));

        final Expr e1 = Expr.not("foo", "a", "b");
        assertTrue(e1.isGround());

        final Expr e2 = new Expr("foo", "A", "B");
        assertFalse(e2.isGround());
    }

    @Test
    public void testNegation() {
        final Expr e1 = Expr.not("foo", "a", "b");
        assertTrue(e1.isNegated());

        final Expr e2 = new Expr("foo", "a", "b");
        assertFalse(e1.equals(e2));
    }

    @Test
    public void testGoodUnification() {
        final Map<String, String> bindings = new HashMap<>();
        final Expr e1 = new Expr("foo", "a", "b");

        final Expr e2 = new Expr("foo", "a", "b");
        assertTrue(e1.unify(e2, bindings));

        bindings.put("X", "b");
        Expr e3 = new Expr("foo", "a", "X");
        assertTrue(e1.unify(e3, bindings));
        assertTrue(e3.unify(e1, bindings));

        final Expr e3a = new Expr("foo", "a", "X");
        assertTrue(e3.unify(e3a, bindings));

        bindings.clear();
        final Expr e4 = new Expr("foo", "Y", "X");
        assertTrue(e1.unify(e4, bindings));
        assertTrue(bindings.get("Y").equals("a"));

        bindings.clear();
        assertTrue(e4.unify(e1, bindings));
        assertTrue(bindings.get("Y").equals("a"));
        assertTrue(bindings.get("X").equals("b"));
    }

    @Test
    public void testBadUnification() {
        final Map<String, String> bindings = new HashMap<>();
        final Expr e1 = new Expr("foo", "a", "b");

        final Expr e2 = new Expr("foo", "a", "b", "c");
        assertFalse(e1.unify(e2, bindings));
        assertFalse(e2.unify(e1, bindings));

        final Expr e3 = new Expr("bar", "a", "b");
        assertFalse(e1.unify(e3, bindings));
        assertFalse(e3.unify(e1, bindings));

        final Expr e4 = new Expr("foo", "A", "b");
        assertTrue(e1.unify(e4, bindings));
        bindings.clear();
        bindings.put("A", "xxxx");
        assertFalse(e1.unify(e4, bindings));
        assertFalse(e4.unify(e1, bindings));
    }

    @Test
    public void testToString() {
        assertAll(
                () -> {
                    Expr e1 = new Expr("foo", "a", "b");
                    assertTrue(e1.toString().equals("foo(a, b)"));
                },
                () -> {
                    Expr e11 = new Expr("foo", "\'a\'", "b");
                    assertEquals("foo('a', b)", e11.toString());
                },
                () -> {
                    Expr e12 = new Expr("foo", "\'a \"c\"\'", "b");
                    assertEquals("foo('a \"c\"', b)", e12.toString());
                },
                () -> {
                    Expr e2 = Expr.not("foo", "a", "b");
                    assertEquals("not foo(a, b)", e2.toString());
                },
                () -> {
                    Expr e3 = new Expr("<>", "X", "Y");
                    assertEquals("X <> Y", e3.toString());
                }
        );
    }

    @Test
    public void testIsBuiltin() {
        final Expr e1 = new Expr("<>", "A", "B");
        assertTrue(e1.isBuiltIn());
        final Expr e2 = new Expr("\"quoted predicate", "A", "B");
        assertFalse(e2.isBuiltIn());
    }

    @Test
    public void testSubstitute() {
        final Expr e1 = new Expr("foo", "X", "Y");
        final Map<String, String> bindings = new HashMap<>();
        bindings.put("X", "a");
        Expr e2 = e1.substitute(bindings);
        assertTrue(e2.getTerms().get(0).equals("a"));
        assertTrue(e2.getTerms().get(1).equals("Y"));
        assertFalse(e2.isNegated());

        final Expr e12 = Expr.not("foo", "X", "Y");
        final Expr e22 = e12.substitute(bindings);
        assertTrue(e22.getTerms().get(0).equals("a"));
        assertTrue(e22.getTerms().get(1).equals("Y"));
        assertTrue(e22.isNegated());
    }

    @Test
    public void testQuotedStrings() {
        final Expr e1 = new Expr("foo", "\"This is a quoted string");
        final Map<String, String> bindings = new HashMap<>();

        assertTrue(e1.toString().equals("foo(\"This is a quoted string\")"));
        bindings.put("X", "\"This is a quoted string");
        bindings.put("Y", "random gibberish");

        Expr e2 = new Expr("foo", "X");
        assertTrue(e1.unify(e2, bindings));

        Expr e3 = new Expr("foo", "Y");
        assertFalse(e1.unify(e3, bindings));

        bindings.clear();
        assertTrue(e1.unify(e2, bindings));
        assertTrue(bindings.get("X").equals("\"This is a quoted string"));

        bindings.clear();
        assertTrue(e2.unify(e1, bindings));
        assertTrue(bindings.get("X").equals("\"This is a quoted string"));
    }

    @Test
    public void testEvalBuiltinEq() throws Exception {

        final Map<String, String> bindings = new HashMap<>();
        final Expr e1 = new Expr("=", "X", "Y");

        bindings.put("X", "hello");
        bindings.put("Y", "hello");
        assertTrue(e1.evalBuiltIn(bindings));

        bindings.clear();
        bindings.put("X", "hello");
        assertTrue(e1.evalBuiltIn(bindings));
        assertTrue(bindings.get("Y").equals("hello"));

        bindings.clear();
        bindings.put("Y", "hello");
        assertTrue(e1.evalBuiltIn(bindings));
        assertTrue(bindings.get("X").equals("hello"));

        bindings.clear();
        bindings.put("X", "hello");
        assertTrue(e1.evalBuiltIn(bindings));
        assertTrue(bindings.get("Y").equals("hello"));

        assertThrows(ExprException.class, () -> {
            bindings.clear();
            e1.evalBuiltIn(bindings);
        });

        bindings.put("X", "100");
        bindings.put("Y", "100.0000");
        assertTrue(e1.evalBuiltIn(bindings));

        bindings.put("X", "100");
        bindings.put("Y", "105");
        assertFalse(e1.evalBuiltIn(bindings));

        bindings.put("X", "100");
        bindings.put("Y", "aaa");
        assertFalse(e1.evalBuiltIn(bindings));

        bindings.put("X", "aaa");
        bindings.put("Y", "100");
        assertFalse(e1.evalBuiltIn(bindings));

        Expr e2 = new Expr("=", "X", "aaa");
        bindings.clear();
        bindings.put("X", "aaa");
        assertTrue(e2.evalBuiltIn(bindings));

        Expr e3 = new Expr("=", "aaa", "Y");
        bindings.clear();
        bindings.put("Y", "aaa");
        assertTrue(e3.evalBuiltIn(bindings));
    }

    @Test
    public void testEvalBuiltinNe() throws Exception {

        final Map<String, String> bindings = new HashMap<>();
        final Expr e1 = new Expr("!=", "X", "Y");
        assertTrue(e1.getPredicate().equals("<>"));

        bindings.put("X", "hello");
        bindings.put("Y", "hello");
        assertFalse(e1.evalBuiltIn(bindings));

        bindings.put("Y", "olleh");
        assertTrue(e1.evalBuiltIn(bindings));

        bindings.put("X", "10");
        bindings.put("Y", "10.000");
        assertFalse(e1.evalBuiltIn(bindings));

        bindings.put("X", "10");
        bindings.put("Y", "10.0001");
        assertTrue(e1.evalBuiltIn(bindings));

        assertThrows(ExprException.class, () -> {
            bindings.clear();
            e1.evalBuiltIn(bindings);
            assertFalse(true);
        });

        assertThrows(ExprException.class, () -> {
            bindings.clear();
            bindings.put("X", "10");
            e1.evalBuiltIn(bindings);
            assertFalse(true);
        });

        assertThrows(ExprException.class, () -> {
            bindings.clear();
            bindings.put("Y", "10");
            e1.evalBuiltIn(bindings);
            assertFalse(true);
        });

        bindings.put("X", "100");
        bindings.put("Y", "aaa");
        assertTrue(e1.evalBuiltIn(bindings));

        bindings.put("X", "aaa");
        bindings.put("Y", "100");
        assertTrue(e1.evalBuiltIn(bindings));
    }

    @Test
    public void testEvalBuiltinOther() throws Exception {

        final Map<String, String> bindings = new HashMap<>();
        bindings.put("X", "100");
        bindings.put("Y", "200");

        final Expr e0 = new Expr("=!=", "X", "Y"); // Bad operator
        assertThrows(ExprException.class, () -> {
            e0.evalBuiltIn(bindings);
            assertTrue(false);
        });

        assertAll(
                () -> {
                    Expr e1 = new Expr(">", "X", "Y");
                    assertFalse(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr(">", "X", "0");
                    assertTrue(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr(">=", "X", "Y");
                    assertFalse(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr(">=", "X", "0");
                    assertTrue(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr(">=", "X", "100");
                    assertTrue(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr("<", "X", "Y");
                    assertTrue(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr("<", "X", "X");
                    assertFalse(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr("<=", "X", "Y");
                    assertTrue(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr("<=", "X", "X");
                    assertTrue(e1.evalBuiltIn(bindings));
                },
                () -> {
                    Expr e1 = new Expr("<=", "Y", "X");
                    assertFalse(e1.evalBuiltIn(bindings));
                },
                () -> {
                    bindings.put("X", "100");
                    bindings.put("Y", "aaa");
                    Expr e1 = new Expr("<", "X", "Y");
                    assertFalse(e1.evalBuiltIn(bindings));
                },
                () -> {
                    bindings.put("X", "aaa");
                    bindings.put("Y", "100");
                    Expr e1 = new Expr("<", "X", "Y");
                    assertTrue(e1.evalBuiltIn(bindings));

                }
        );
    }
}
