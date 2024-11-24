package za.co.wstoop.jatalog.engine;

import org.junit.Test;
import za.co.wstoop.jatalog.Expr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static za.co.wstoop.jatalog.Expr.expr;

public class IndexedSetAlternativeTest {
    @Test
    public void testBase() {
        IndexedSetAlternative<Expr, String> indexedSet = new IndexedSetAlternative<>();

        assertTrue(indexedSet.isEmpty());
        indexedSet.add(expr("foo", "a"));
        indexedSet.add(expr("foo", "b"));
        indexedSet.add(expr("foo", "c"));
        indexedSet.add(expr("bar", "a"));
        indexedSet.add(expr("bar", "b"));

        assertFalse(indexedSet.isEmpty());

        assertTrue(indexedSet.getIndexes().size() == 2);
        assertTrue(indexedSet.getIndexes().contains("foo"));
        assertTrue(indexedSet.getIndexes().contains("bar"));
        assertFalse(indexedSet.getIndexes().contains("baz"));

        java.util.Set<Expr> set = indexedSet.getIndexed("foo");
        assertEquals(3, set.size());
        assertTrue(set.contains(expr("foo", "a")));
        assertTrue(set.contains(expr("foo", "b")));
        assertTrue(set.contains(expr("foo", "c")));
        assertFalse(set.contains(expr("foo", "d")));

        assertTrue(indexedSet.contains(expr("bar", "a")));
        indexedSet.remove(expr("bar", "a"));
        assertFalse(indexedSet.contains(expr("bar", "a")));

        java.util.Set<Expr> toRemove = new java.util.HashSet<>();
        toRemove.add(expr("foo", "a"));
        toRemove.add(expr("bar", "b"));

        assertTrue(indexedSet.containsAll(toRemove));
        toRemove.add(expr("bar", "c"));
        assertFalse(indexedSet.containsAll(toRemove));

        indexedSet.removeAll(toRemove);

        assertFalse(indexedSet.getIndexes().contains("bar"));
        assertFalse(indexedSet.contains(expr("foo", "a")));
        assertFalse(indexedSet.contains(expr("bar", "b")));

        assertFalse(indexedSet.removeAll(toRemove));

        indexedSet.clear();
        assertEquals(0, indexedSet.size());
        assertTrue(indexedSet.isEmpty());
        assertEquals(0, indexedSet.getIndexes().size());
    }

    @Test
    public void testCreateIndexedSetAlternative() {
        IndexedSetAlternative<Expr, String> indexedSet = create();

        indexedSet.add(expr("foo", "a"));
        indexedSet.add(expr("foo", "b"));
        indexedSet.add(expr("foo", "c"));
        indexedSet.add(expr("bar", "a"));
        indexedSet.add(expr("bar", "b"));

        assertEquals(2, indexedSet.getIndexes().size());
        assertEquals(3, indexedSet.getIndexed("foo").size());
        assertEquals(2, indexedSet.getIndexed("bar").size());
        assertEquals(0, indexedSet.getIndexed("foobar").size());

        assertEquals(5, indexedSet.size());
    }

    @Test
    public void testAdd() {
        IndexedSetAlternative<Expr, String> indexedSet = create();

        indexedSet.add(expr("foobar", "a"));
        assertEquals(3, indexedSet.getIndexed("foo").size());
        assertEquals(2, indexedSet.getIndexed("bar").size());
        assertEquals(1, indexedSet.getIndexed("foobar").size());
        assertEquals(6, indexedSet.size());
    }

    @Test
    public void testRemoveExprFooA() {
        IndexedSetAlternative<Expr, String> indexedSet = create();

        boolean rc = indexedSet.remove(expr("foo", "a"));
        assertTrue(rc);
        assertEquals(2, indexedSet.getIndexed("foo").size());
        assertEquals(2, indexedSet.getIndexed("bar").size());
        assertEquals(0, indexedSet.getIndexed("foobar").size());
        assertEquals(4, indexedSet.size());
    }

    @Test
    public void testRemoveAllExprFooA() {
        IndexedSetAlternative<Expr, String> indexedSet = create();

        boolean rc = indexedSet.removeAll(java.util.Arrays.asList(expr("foo", "a")));
        assertTrue(rc);
        assertEquals(2, indexedSet.getIndexed("foo").size());
        assertEquals(2, indexedSet.getIndexed("bar").size());
        assertEquals(0, indexedSet.getIndexed("foobar").size());
        assertEquals(4, indexedSet.size());
    }

    @Test
    public void testRetainAllExprFooA() {
        IndexedSetAlternative<Expr, String> indexedSet = create();

        boolean rc = indexedSet.retainAll(java.util.Arrays.asList(expr("foo", "a")));
        assertTrue(rc);
        assertEquals(1, indexedSet.getIndexed("foo").size());
        assertEquals(0, indexedSet.getIndexed("bar").size());
        assertEquals(0, indexedSet.getIndexed("foobar").size());
        assertEquals(1, indexedSet.size());
    }

    private IndexedSetAlternative<Expr, String> create() {
        IndexedSetAlternative<Expr, String> indexedSet = new IndexedSetAlternative<>();

        assertTrue(indexedSet.isEmpty());
        indexedSet.add(expr("foo", "a"));
        indexedSet.add(expr("foo", "b"));
        indexedSet.add(expr("foo", "c"));
        indexedSet.add(expr("bar", "a"));
        indexedSet.add(expr("bar", "b"));
        assertFalse(indexedSet.isEmpty());
        return indexedSet;
    }
}