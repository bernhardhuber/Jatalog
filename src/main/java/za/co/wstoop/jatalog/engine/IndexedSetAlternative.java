package za.co.wstoop.jatalog.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IndexedSetAlternative<E extends Indexable<I>, I> implements Set<E> {
    private final Set<E> contents;

    private final Map<I, Set<E>> index;

    public IndexedSetAlternative() {
        this(Collections.emptySet());
    }

    public IndexedSetAlternative(Collection<E> elements) {
        this.index = new HashMap<>();
        this.contents = new HashSet<>();
        this.addAll(elements);
    }

    /**
     * Retrieves the subset of the elements in the set with the
     * specified index.
     *
     * @param key The indexed element
     * @return The specified subset
     */
    public Set<E> getIndexed(I key) {
        Set<E> elements = index.get(key);
        if (elements == null) {
            return Collections.emptySet();
        }
        return elements;
    }

    public Collection<I> getIndexes() {
        return index.keySet();
    }

    @Override
    public int size() {
        return this.contents.size();
    }

    @Override
    public boolean isEmpty() {
        return this.contents.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.contents.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.contents.iterator();
    }

    @Override
    public Object[] toArray() {
        return contents.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return contents.toArray(a);
    }

    @Override
    public boolean add(E e) {
        boolean rc = contents.add(e);
        I indexv = e.index();
        Set<E> setOfE = index.get(indexv);
        if (setOfE != null) {
            setOfE.add(e);
        } else {
            setOfE = new HashSet<>();
            setOfE.add(e);
            index.put(indexv, setOfE);
        }
        return rc;
    }

    @Override
    public boolean remove(Object o) {
        boolean changed = contents.remove(o);
        if (changed) {
            // This makes the remove O(n), but you need it like this if remove()
            // is to work through an iterator.
            // It doesn't really matter, since Jatalog doesn't use this method
            // reindexAfterRemove();
            E e = (E) o;
            Set<E> setOfE = index.get(e.index());
            setOfE.remove(e);
        }
        return changed;
    }

    @Override
    public boolean containsAll(Collection<?> elements) {
        return contents.containsAll(elements);
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        int mode = 2;
        if (mode == 1) {
            return addAll1(elements);
        } else if (mode == 2) {
            return addAll2(elements);
        } else {
            return addAll1(elements);
        }
    }

    boolean addAll1(Collection<? extends E> elements) {
        boolean result = false;
        for (E element : elements) {
            if (add(element)) {
                result = true;
            }
        }
        return result;
    }

    boolean addAll2(Collection<? extends E> elements) {
        boolean result = contents.addAll(elements);
        if (result) {
            reindexAfterAdd();
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = contents.retainAll(c);
        if (changed) {
            reindexAfterRemove();
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = contents.removeAll(c);
        if (changed) {
            reindexAfterRemove();
        }
        return changed;
    }

    @Override
    public void clear() {
        index.clear();
        contents.clear();
    }

    void reindexAfterRemove() {
        index.clear();
        for (E element : contents) {
            Set<E> elements = index.get(element.index());
            if (elements == null) {
                elements = new HashSet<E>();
                index.put(element.index(), elements);
            }
            elements.add(element);
        }
    }

    private void reindexAfterAdd() {
        contents.forEach(e -> {
            I elementIndexV = e.index();
            Set<E> indexV = index.get(elementIndexV);
            if (indexV == null) {
                indexV = new java.util.HashSet<>();
                index.put(elementIndexV, indexV);
            }
            indexV.add(e);

        });
    }
}
