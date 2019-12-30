package base;

import java.util.Iterator;
import java.util.Vector;
import java.util.EmptyStackException;

/**
 * A <code>Stack</code> is a collection of {@link Stone} objects.
 * <br><br>
 *
 * We maintain the invariant that at index 0 of this vector is always
 * a sentinel stone of type <code>FLAT</code>, owned by player -1.
 */
public class Stack extends Vector<Stone> {

    /**
     * Creates an "empty" Stack with initial capacity
     * (as specified by the Vector class).
     */
    public Stack(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
        add(new Stone(-1, Stone.Type.FLAT));
    }

    /**
     * @return a copy of this Stack
     */
    public Stack copy() {
        Stack copy = new Stack(capacity(), capacityIncrement);
        for (Stone stone : this) {
            copy.add(stone.copy());
        }
        return copy;
    }

    /**
     * Looks at the stone at the top of this stack without removing it.
     *
     * @return  the stone at the top of this stack.
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
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Will always be >= 1 because of the sentinel stone.
     * Identical to <code>Vector.size()</code>. See
     * <code>Stack.size()</code> for the number of
     * player-owned stones in this stack.
     * @return The "technical" boardSize of this stack.
     */
    public int tSize() {
        return super.size();
    }

    /**
     * See <code>tSize()</code> if looking for the "technical"
     * boardSize of this stack, i.e. including the sentinel stone.
     * @return The number of player-owned stones in this stack.
     */
    @Override
    public int size() {
        return tSize() - 1;
    }

    /**
     * Removes stones starting from <code>fromIndex</code>, inclusive, all the way to
     * <code>toIndex</code>, exclusive. Removes <code>toIndex</code> - <code>fromIndex</code>
     * stones. (Note <code>fromIndex</code> can never be 0, we cannot remove the sentinel stone.)
     * @param fromIndex index of first element to remove, inclusive.
     * @param toIndex index of last element to remove, exclusive.
     * @throws IllegalArgumentException if <code>fromIndex</code> is 0.
     */
    @Override
    public void removeRange(int fromIndex, int toIndex) {
        if (fromIndex == 0) {
            throw new IllegalArgumentException("Cannot remove sentinel stone.");
        }
        super.removeRange(fromIndex, toIndex);
    }

    /**
     * This method allows us to iterate over the top n stones
     * with the top stone being last.
     * @param n the number of stones from the top
     * @return An iterator over the last n stones
     * @throws IllegalArgumentException if n > <code>size()</code>.
     */
    public Iterator<Stone> stoneIterator(int n) {
        if (n > size()) {
            throw new IllegalArgumentException("Not enough player stones to iterate over.");
        }
        return new StoneIterator(tSize() - n);
    }

    private class StoneIterator implements Iterator<Stone> {
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
