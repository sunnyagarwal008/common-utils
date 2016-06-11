/*
 *  @version     1.0, Dec 21, 2011
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CollectionUtils {

    public static <T> boolean isEmpty(Collection<T> c) {
        return c == null || c.size() == 0;
    }

    /**
     * @param <T>
     * @param c
     * @return
     */
    public static <T> List<T> asList(Collection<T> c) {
        if (c instanceof List) {
            return (List<T>) c;
        } else {
            List<T> list = new ArrayList<T>();
            list.addAll(c);
            return list;
        }
    }

    public static <T> Set<T> asSortedSet(T[] c) {
        if (c == null) {
            return null;
        }
        Set<T> set = new LinkedHashSet<T>(c.length);
        for (T t : c) {
            set.add(t);
        }
        return set;
    }

    public static <T> Set<T> asSet(T[] c) {
        if (c == null) {
            return null;
        }
        Set<T> set = new HashSet<T>(c.length);
        for (T t : c) {
            set.add(t);
        }
        return set;
    }

    public static <T> Set<T> asSet(Collection<T> c) {
        if (c == null) {
            return null;
        }
        Set<T> set = new HashSet<T>(c.size());
        for (T t : c) {
            set.add(t);
        }
        return set;
    }

    /**
     * @param <T>
     * @param c
     * @param comparator
     * @return the largest element in collection
     */
    public static <T> T findMax(Collection<T> c, Comparator<T> comparator) {
        if (c.size() == 0) {
            throw new IllegalArgumentException("list is empty");
        }
        T max = c.iterator().next();
        for (T t : c) {
            if (comparator.compare(t, max) > 0) {
                max = t;
            }
        }
        return max;
    }

    /**
     * @param <T>
     * @param c
     * @param comparator
     * @return the smallest element in collection
     */
    public static <T> T findMin(Collection<T> c, Comparator<T> comparator) {
        if (c.size() == 0) {
            throw new IllegalArgumentException("list is empty");
        }
        T min = c.iterator().next();
        for (T t : c) {
            if (comparator.compare(t, min) < 0) {
                min = t;
            }
        }
        return min;
    }

    public static <T> Iterator<List<T>> sublistIterator(List<T> list, int sublistLength) {
        return new SublistIterator<T>(list, sublistLength);
    }

    public static class SublistIterator<T> implements Iterator<List<T>> {
        private final List<T> list;
        private final int     sublistLength;
        private int           currentIndex = 0;

        public SublistIterator(List<T> list, int sublistLength) {
            this.list = list;
            this.sublistLength = sublistLength;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return currentIndex < list.size();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public List<T> next() {
            List<T> sublist = list.subList(currentIndex, Math.min(currentIndex + sublistLength, list.size()));
            currentIndex += sublist.size();
            return sublist;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove unsupported on SublistIterator");
        }

    }
}
