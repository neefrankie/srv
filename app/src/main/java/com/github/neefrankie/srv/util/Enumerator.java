package com.github.neefrankie.srv.util;

import java.util.*;

public final class Enumerator<E> implements Enumeration<E> {

    private Iterator<E> iterator = null;

    public Enumerator(Iterator<E> iterator) {
        super();
        this.iterator = iterator;
    }

    public Enumerator(Collection<E> collection) {
        this(collection.iterator());
    }

    @Override
    public boolean hasMoreElements() {
        return (iterator.hasNext());
    }

    @Override
    public E nextElement() throws NoSuchElementException {
        return (iterator.next());
    }
}
