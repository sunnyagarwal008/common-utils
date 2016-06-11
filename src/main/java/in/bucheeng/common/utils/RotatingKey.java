package in.bucheeng.common.utils;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;

/**
 * A virtual key that rotates in a round-robin fashion over a set of actual
 * keys.
 * 
 */
public class RotatingKey {

    private Iterator<String> itr;

    public RotatingKey(List<String> apiKeys) {
        this.itr = Iterators.cycle(apiKeys);
    }

    public String get() {
        return itr.next();
    }
}
