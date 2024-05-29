package nl.han.ica.datastructures;


import java.util.Iterator;

public class HANLinkedListIterator<T> implements Iterator<T> {
    private HANLinkedListNode<T> current;

    public HANLinkedListIterator(HANLinkedListNode<T> first) {
        current = first;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public T next() {
        if (!hasNext())
            throw new IllegalStateException("No more elements");

        HANLinkedListNode<T> node = current;
        current = current.getNext();
        return node.getValue();
    }

}