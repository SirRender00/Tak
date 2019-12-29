package base;

import java.util.Iterator;
import java.util.Vector;
import java.util.EmptyStackException;

/**
 * The stack is a collection of {@link Stone} objects.
 * Every stack is initialized with sentinel stone at the
 * start of type FLAT, owned by player -1.
 */
public class Stack extends Vector<Stone> {

    /**
     * Creates an "empty" Stack with initial capacity
     * (as specified by the Vector class).
     */
    public Stack(int initialCapacity) {
        super(initialCapacity);
        addElement(new Stone(-1, Stone.Type.FLAT));
    }

    /**
     * A deep copy of a stack
     * @param stack The stack to copy
     */
    public Stack(Stack stack) {
        super(stack.capacity());
        for (Stone stone : stack) {
            addElement(new Stone(stone));
        }
    }

    /**
     * Looks at the stone at the top of this stack without removing it.
     *
     * @return  the stone at the top of this stack (the last item
     *          of the {@code Vector} object).
     */
    public synchronized Stone peek() {
        int len = tSize();

        if (len == 0) {
            throw new EmptyStackException();
        }
        return elementAt(len - 1);
    }

    /**
     * Tests if this stack is empty.
     *
     * @return  {@code true} if and only if this stack contains
     *          no player-owned stones; {@code false} otherwise.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Will always be >= 1 because of the sentinel stone.
     * Identical to <code>Vector.size()</code>. See
     * <code>Stack.size()</code> for the number of
     * player-owned stones in this stack.
     * @return The "technical" size of this stack.
     */
    public int tSize() {
        return super.size();
    }

    /**
     * See <code>tSize()</code> if looking for the "technical"
     * size of this stack, i.e. including the sentinel stone.
     * @return The number of player-owned stones in this stack.
     */
    @Override
    public int size() {
        return tSize() - 1;
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        if (fromIndex == 0) {
            throw new IllegalArgumentException("Cannot remove sentinel stone.");
        }
        super.removeRange(fromIndex, toIndex);
    }

    /**
     * @param n the number of stones from the top
     * @return An iterator over the last n stones
     */
    public Iterator<Stone> stoneIterator(int n) {
        return new StoneIterator(tSize() - n);
    }

    public class StoneIterator implements Iterator<Stone> {
        int n;

        StoneIterator(int n) {
            this.n = n;
        }

        @Override
        public boolean hasNext() {
            return n < tSize();
        }

        @Override
        public Stone next() {
            return elementAt(n++);
        }
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 1224463164541339165L;
}
