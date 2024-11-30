package za.co.wstoop.jatalog.output;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pi
 */
class MapBuilder<K, V> {
    
    Map<K, V> m;

    MapBuilder() {
        this.m = new HashMap<>();
    }

    MapBuilder<K, V> put(K k, V v) {
        this.m.put(k, v);
        return this;
    }

    Map<K, V> build() {
        return this.m;
    }
    
}
