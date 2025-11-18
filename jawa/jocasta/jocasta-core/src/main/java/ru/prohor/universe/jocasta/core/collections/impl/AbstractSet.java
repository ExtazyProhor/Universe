package ru.prohor.universe.jocasta.core.collections.impl;

import ru.prohor.universe.jocasta.core.collections.api.SetF;

import java.util.Iterator;
import java.util.Set;

public abstract class AbstractSet<T> implements SetF<T> {
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Set<?>) {
            Set<?> s = (Set<?>) object;

            try {
                return size() == s.size() && containsAll(s);
            } catch (ClassCastException cce) {
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        int result = 0;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            Object next = it.next();
            result += next == null ? 0 : next.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return toStringImpl();
    }
}
