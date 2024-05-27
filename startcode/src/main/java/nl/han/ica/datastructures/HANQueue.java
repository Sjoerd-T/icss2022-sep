package nl.han.ica.datastructures;

public class HANQueue<T> implements IHANQueue<T> {
    private final HANLinkedList<T> queueList;

    public HANQueue() {
        this.queueList = new HANLinkedList<>();
    }

    @Override
    public void clear() {
        queueList.clear();
    }

    @Override
    public boolean isEmpty() {
        return queueList.getSize() == 0;
    }

    @Override
    public void enqueue(T value) {
        queueList.addFirst(value);
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        // Since we're adding elements to the front of the list (index 0), we dequeue from the end of the list (index size - 1)
        T value = queueList.get(queueList.getSize() - 1);
        queueList.delete(queueList.getSize() - 1);
        return value;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        // Peek at the last element of the list (index size - 1)
        return queueList.get(queueList.getSize() - 1);
    }

    @Override
    public int getSize() {
        return queueList.getSize();
    }
}