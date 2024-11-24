package za.co.wstoop.jatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestUtils {

    private TestUtils() {
    }

    public static Jatalog createDatabase() throws DatalogException {

        Jatalog jatalog = new Jatalog();

        jatalog.fact("parent", "a", "aa")
                .fact("parent", "a", "ab")
                .fact("parent", "aa", "aaa")
                .fact("parent", "aa", "aab")
                .fact("parent", "aaa", "aaaa")
                .fact("parent", "c", "ca");

        jatalog.rule(Expr.expr("ancestor", "X", "Y"),
                Expr.expr("parent", "X", "Z"),
                Expr.expr("ancestor", "Z", "Y"))
                .rule(Expr.expr("ancestor", "X", "Y"),
                        Expr.expr("parent", "X", "Y"))
                .rule(Expr.expr("sibling", "X", "Y"),
                        Expr.expr("parent", "Z", "X"),
                        Expr.expr("parent", "Z", "Y"),
                        Expr.ne("X", "Y"))
                .rule(Expr.expr("related", "X", "Y"),
                        Expr.expr("ancestor", "Z", "X"),
                        Expr.expr("ancestor", "Z", "Y"));

        return jatalog;
    }

    public static boolean mapContains(Map<String, String> map, String key, String value) {
        return Optional.ofNullable(map.get(key))
                .map(v -> v.equals(value))
                .orElse(false);
    }

    public static boolean mapContains(Map<String, String> haystack, Map<String, String> needle) {
        for (String key : needle.keySet()) {
            if (!haystack.containsKey(key)) {
                return false;
            }
            if (!haystack.get(key).equals(needle.get(key))) {
                return false;
            }
        }
        return true;
    }

    public static boolean answerContains(Collection<Map<String, String>> answers, String... kvPairs) throws IllegalArgumentException {
        Map<String, String> needle = new HashMap<>();
        if (kvPairs.length % 2 != 0) {
            throw new IllegalArgumentException("kvPairs must be even");
        }
        for (int i = 0; i < kvPairs.length / 2; i++) {
            String k = kvPairs[i * 2];
            String v = kvPairs[i * 2 + 1];
            needle.put(k, v);
        }
        for (Map<String, String> answer : answers) {
            if (mapContains(answer, needle)) {
                return true;
            }
        }
        return false;
    }
}
