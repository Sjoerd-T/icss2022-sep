package nl.han.ica.datastructures;

public class HANLinkedListNode<T> {
    T data;
    HANLinkedListNode<T> next;

    HANLinkedListNode(T data) {
        this.data = data;
        this.next = null;
    }

    public HANLinkedListNode<T> getNext() {
        return next;
    }

    public T getValue() {
        return data;
    }
}
