package base;

import java.util.Iterator;
import java.util.Vector;
import java.util.EmptyStackException;

public class Stack extends Vector<Stone> {

    /**
     * Creates an empty Stack with initial capacity.
     */
    public Stack(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     * @return  The object at the top of this stack (the last item
     *          of the {@code Vector} object).
     * @throws  EmptyStackException  if this stack is empty.
     */
    public synchronized Stone pop() {
        Stone obj;
        int len = size();

        obj = peek();
        removeElementAt(len - 1);

        return obj;
    }

    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     *
     * @return  the object at the top of this stack (the last item
     *          of the {@code Vector} object).
     * @throws  EmptyStackException  if this stack is empty.
     */
    public synchronized Stone peek() {
        int len = size();

        if (len == 0) {
            throw new EmptyStackException();
        }
        return elementAt(len - 1);
    }

    /**
     * Tests if this stack is empty.
     *
     * @return  {@code true} if and only if this stack contains
     *          no items; {@code false} otherwise.
     */
    public boolean empty() {
        return size() == 0;
    }

    /**
     * @param n return the last n stones from the top
     * @return An iterator over the last n stones
     */
    public Iterator<Stone> stoneIterator(int n) {
        return new StoneIterator(size() - n);
    }

    public class StoneIterator implements Iterator<Stone> {
        int n;

        public StoneIterator(int n) {
            this.n = n;
        }

        @Override
        public boolean hasNext() {
            return n < size();
        }

        @Override
        public Stone next() {
            return elementAt(n++);
        }
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 1224463164541339165L;
}
