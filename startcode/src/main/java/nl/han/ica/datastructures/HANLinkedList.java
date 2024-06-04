package nl.han.ica.datastructures;

import java.util.Iterator;

public class HANLinkedList<T> implements IHANLinkedList<T>
{
    private HANLinkedListNode<T> head;
    private int size;

    public HANLinkedList()
    {
        this.head = null;
        this.size = 0;
    }

    @Override
    public void addFirst(T value)
    {
        HANLinkedListNode<T> newHANLinkedListNode = new HANLinkedListNode<>(value);
        newHANLinkedListNode.next = head;
        head = newHANLinkedListNode;
        size++;
    }

    @Override
    public void clear()
    {
        head = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value)
    {
        if (index < 0 || index > size)
        {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        if (index == 0)
        {
            addFirst(value);
        }
        else
        {
            HANLinkedListNode<T> newHANLinkedListNode = new HANLinkedListNode<>(value);
            HANLinkedListNode<T> current = head;
            for (int i = 0; i < index - 1; i++)
            {
                current = current.next;
            }
            newHANLinkedListNode.next = current.next;
            current.next = newHANLinkedListNode;
            size++;
        }
    }

    @Override
    public void delete(int pos)
    {
        if (pos < 0 || pos >= size)
        {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        if (pos == 0)
        {
            removeFirst();
        }
        else
        {
            HANLinkedListNode<T> current = head;
            for (int i = 0; i < pos - 1; i++)
            {
                current = current.next;
            }
            current.next = current.next.next;
            size--;
        }
    }

    @Override
    public T get(int pos)
    {
        if (pos < 0 || pos >= size)
        {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        HANLinkedListNode<T> current = head;
        for (int i = 0; i < pos; i++)
        {
            current = current.next;
        }
        return current.data;
    }

    @Override
    public void removeFirst()
    {
        if (head != null)
        {
            head = head.next;
            size--;
        }
    }

    @Override
    public T getFirst()
    {
        if (head == null)
        {
            throw new IllegalStateException("List is empty");
        }
        return head.data;
    }

    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public Iterator<T> iterator()
    {
        return new HANLinkedListIterator<>(head);
    }
}