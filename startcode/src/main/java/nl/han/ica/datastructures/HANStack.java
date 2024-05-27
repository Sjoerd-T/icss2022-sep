package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {
    private final HANLinkedList<T> stackList;

    public HANStack() {
        this.stackList = new HANLinkedList<>();
    }

    @Override
    public void push(T value) {
        stackList.addFirst(value);
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        T value = stackList.getFirst();
        stackList.removeFirst();
        return value;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stackList.getFirst();
    }

    private boolean isEmpty() {
        return stackList.getSize() == 0;
    }
}