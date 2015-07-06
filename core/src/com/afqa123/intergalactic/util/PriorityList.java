package com.afqa123.intergalactic.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Ordered list implementation.
 * 
 * @param <E> The element type.
 */
public class PriorityList<E> extends LinkedList<E> {

    private final Comparator<E> comparator;

    public PriorityList(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean add(E e) {
        ListIterator<E> it = listIterator();
        while (it.hasNext()) {
            E el = it.next();
            if (comparator.compare(e, el) < 0) {
                // at this point, we've gone too far - the new element's value
                // is less than the current element's value, so move back
                it.previous();
                break;
            }
        }
        it.add(e);
        return true;
    }
}