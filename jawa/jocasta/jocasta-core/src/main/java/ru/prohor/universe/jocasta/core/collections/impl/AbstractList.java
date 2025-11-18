package ru.prohor.universe.jocasta.core.collections.impl;

import jakarta.annotation.Nonnull;
import ru.prohor.universe.jocasta.core.collections.api.IteratorF;
import ru.prohor.universe.jocasta.core.collections.api.ListF;
import ru.prohor.universe.jocasta.core.functional.MonoPredicate;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractList<T> implements ListF<T> {
    protected transient int modCount;

    private class SimpleListIterator implements IteratorF<T> {
        int pos = -1;
        int expectedModCount;
        int lastPosition = -1;

        SimpleListIterator() {
            super();
            expectedModCount = modCount;
        }

        public boolean hasNext() {
            return pos + 1 < size();
        }

        public T next() {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            try {
                T result = get(pos + 1);
                lastPosition = ++pos;
                return result;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (this.lastPosition == -1)
                throw new IllegalStateException();
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            try {
                AbstractList.this.remove(lastPosition);
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
            expectedModCount = modCount;
            if (pos == lastPosition)
                pos--;
            lastPosition = -1;
        }

        @Override
        public ListF<T> toList() {
            return AbstractList.this.drop(lastPosition + 1);
        }

        @Override
        public IteratorF<T> drop(int count) {
            return toList().drop(count).iterator();
        }

        @Override
        public IteratorF<T> take(int count) {
            return toList().take(count).iterator();
        }

        @Override
        public IteratorF<T> dropWhile(MonoPredicate<? super T> p) {
            while (hasNext()) {
                T e = next();
                if (!p.test(e))
                    return AbstractList.this.drop(lastPosition).iterator();
            }
            return new EmptyIterator<>();
        }
    }

    private class FullListIterator extends SimpleListIterator implements ListIterator<T> {
        FullListIterator(int start) {
            super();
            if (start < 0 || start > size())
                throw new IndexOutOfBoundsException();
            pos = start - 1;
        }

        public void add(T object) {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            try {
                AbstractList.this.add(pos + 1, object);
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
            pos++;
            lastPosition = -1;
            if (modCount != expectedModCount)
                expectedModCount++;
        }

        public boolean hasPrevious() {
            return pos >= 0;
        }

        public int nextIndex() {
            return pos + 1;
        }

        public T previous() {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            try {
                T result = get(pos);
                lastPosition = pos;
                pos--;
                return result;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public int previousIndex() {
            return pos;
        }

        public void set(T object) {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            try {
                AbstractList.this.set(lastPosition, object);
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof List<?> list))
            return false;
        if (list.size() != size())
            return false;
        Iterator<?> it1 = iterator(), it2 = list.iterator();
        while (it1.hasNext())
            if (!(Objects.equals(it1.next(), it2.next())))
                return false;
        return true;
    }

    public abstract T get(int location);

    @Override
    public int hashCode() {
        int result = 1;
        for (Object object : this)
            result = (31 * result) + (object == null ? 0 : object.hashCode());
        return result;
    }

    @Nonnull
    @Override
    public IteratorF<T> iterator() {
        return new SimpleListIterator();
    }

    public int lastIndexOf(Object object) {
        ListIterator<?> it = listIterator(size());
        if (object != null)
            while (it.hasPrevious())
                if (object.equals(it.previous()))
                    return it.nextIndex();
                else
                    while (it.hasPrevious())
                        if (it.previous() == null)
                            return it.nextIndex();
        return -1;
    }

    @Nonnull
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Nonnull
    public ListIterator<T> listIterator(int location) {
        return new FullListIterator(location);
    }

    @Nonnull
    public ListF<T> subList(int start, int end) {
        if (start < 0 || end > size())
            throw new IndexOutOfBoundsException();
        if (start > end)
            throw new IllegalArgumentException("start more than end");
        if (this instanceof RandomAccess)
            return new SubAbstractListRandomAccess<>(this, start, end);
        return new SubAbstractList<>(this, start, end);
    }

    @Override
    public String toString() {
        return toStringImpl();
    }
}
