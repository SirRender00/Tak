package base.move;

import base.Stone;
import base.Tak;
import structures.Direction;

import java.util.*;

/**
 * Utility functions related to the generation of Tak {@link Move} objects.
 * Includes creation of {@link PlaceMove} moves, {@link StackMove} moves,
 * parsing moves from a string by PTN convention
 * https://www.reddit.com/r/Tak/wiki/portable_tak_notation, and iterating
 * over all possible moves for the current player given a Tak instance.
 */
public final class MoveFactory {

    private MoveFactory() {

    }

    /**
     * A map of (number of pieces) --> a map of (number of spaces --> list of all permutations)
     * Used so we don't have to compute stack permutations all the time.
     */
    private static HashMap<Integer, HashMap<Integer, List<int[]>>> allStackVals = new HashMap<>();

    /**
     * @param x The x coord of the move
     * @param y The y coord of the move
     * @param type The type of stone to place
     * @return The {@code PlaceMove} object as specified
     */
    public static PlaceMove placeMove(int x, int y, Stone.Type type) {
        return new PlaceMove(x, y, type);
    }

    /**
     * @param x The x coord to pickup stones
     * @param y The y coord to pickup stones
     * @param dir The direction to drop stones
     * @param pickup The amount to pick up
     * @param vals The amount to drop down at each step, going on for <code>vals.length</code>
     * @return The {@code StackMove} object as specified
     */
    public static StackMove stackMove(int x, int y, Direction dir, int pickup, int[] vals) {
        return new StackMove(x, y, dir, pickup, vals);
    }

    /**
     * Parses a string according to PTN convention
     * https://www.reddit.com/r/Tak/wiki/portable_tak_notation.
     * @param str The string to parse in PTN notation.
     * @return The {@code Move} object associated
     */
    public static Move parseMove(String str) {
        char[] chars = str.toCharArray();

        if (Character.isDigit(chars[0])) {
            int[] vals = new int[chars.length - 4];
            for (int i = 4; i < chars.length; i++) {
                if (!Character.isDigit(chars[i])) {
                    throw new RuntimeException("Error in parsing move");
                }

                vals[i - 4] = Character.getNumericValue(chars[i]);
            }

            return new StackMove(
                    charToFile(chars[1]),
                    charToRow(chars[2]),
                    charToDirection(chars[3]),
                    Character.getNumericValue(chars[0]),
                    vals);
        } else if (Character.isLowerCase(chars[0])) {
            if (chars.length != 2) {
                throw new RuntimeException("Error in parsing move");
            }

            return placeMove(
                    charToFile(chars[0]),
                    charToRow(chars[1]),
                    Stone.Type.FLAT);
        } else if (Character.isAlphabetic(chars[0])) {
            if (chars.length >= 4) {
                throw new RuntimeException("Error in parsing move");
            }

            return placeMove(
                    charToFile(chars[1]),
                    charToRow(chars[2]),
                    charToStoneType(chars[0]));
        } else {
            throw new RuntimeException("Error in parsing move");
        }
    }

    public static int charToFile(char c) {
        return (int) c - 'a';
    }

    public static int charToRow(char c) {
        return Character.getNumericValue(c) - 1;
    }

    public static Direction charToDirection(char c) {
        if (c == '-') {
            return Direction.DOWN;
        } else if (c == '+') {
            return Direction.UP;
        } else if (c == '<') {
            return Direction.LEFT;
        } else if (c == '>') {
            return Direction.RIGHT;
        } else {
            return null;
        }
    }

    public static Stone.Type charToStoneType(char c) {
        if (c == 'F') {
            return Stone.Type.FLAT;
        } else if (c == 'S') {
            return Stone.Type.STANDING;
        } else if (c == 'C') {
            return Stone.Type.CAP;
        } else {
            return null;
        }
    }

    /**
     * @param tak The tak instance to generate moves over.
     * @return An iterator over all the possible moves for the
     * current player.
     */
    public static MoveIterator allPossibleMoves(Tak tak) {
        return new MoveIterator(tak);
    }

    /**
     * @param tak The tak instance
     * @param x The x coord
     * @param y The y ooord
     * @param dir The Direction to probe in
     * @return The number of squares from (x,y) in the specified direction
     * in between a non-flat stone or the boundary of the tak board.
     * (If x,y is not in bounds then this function returns 0. If x,y is on
     * an edge then this function returns 0.)
     */
    private static int lengthToNearestStop(Tak tak, int x, int y, Direction dir) {
        for (int i = 0; i < tak.size(); i++) {
            x += dir.dx;
            y += dir.dy;

            if (!tak.inBounds(x, y)) {
                return i;
            }

            if (!tak.getStackAt(x, y).peek().type.equals(Stone.Type.FLAT)) {
                return i;
            }
        }

        return tak.size() - 1;
    }

    /**
     * A combination of {@code PlaceMoveIterator} and {@code StackMoveIterator}
     * and therefore represents the iterator over all possible moves given a
     * certain Tak instance.
     */
    public static class MoveIterator implements Iterator<Move>, Iterable<Move> {

        StackMoveIterator sIter;
        PlaceMoveIterator pIter;

        MoveIterator(Tak tak) {
            pIter = new PlaceMoveIterator(tak);
            sIter = new StackMoveIterator(tak);
        }

        @Override
        public Iterator<Move> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return pIter.hasNext() || sIter.hasNext();
        }

        @Override
        public Move next() {
            if (pIter.hasNext()) {
                return pIter.next();
            } else if (sIter.hasNext()) {
                return sIter.next();
            } else {
                throw new NoSuchElementException("Iterator does not have next element");
            }
        }
    }

    /**
     * An iterator over all possible PlaceMoves given a Tak instance
     */
    public static class PlaceMoveIterator implements Iterator<PlaceMove> {
        Tak tak;

        // place moves
        // x,y keeps track of each square
        int x = 0;
        int y = 0;

        // keep tracks of type of stones
        int m = 0;

        boolean placeHasNext;

        public PlaceMoveIterator(Tak tak) {
            this.tak = new Tak(tak);
            preparePlace();
        }

        void preparePlace() {
            //for each type of stone
            for (int u = m; u < Stone.Type.values().length; u++) {
                if (tak.getCurrentPlayer().getRemainingStones(Stone.Type.values()[u]) <= 0) {
                    continue;
                }

                //find where there is an empty stack
                for (int i = x; i < tak.size(); i++) {
                    for (int j = y; j < tak.size(); j++) {
                        if (tak.getStackAt(i, j).isEmpty()) {
                            m = u;
                            x = i;
                            y = j;
                            placeHasNext = true;
                            return;
                        }
                    }
                    y = 0;
                }

                // if we can't find an empty space, we're done
                break;
            }

            //if we finish the above loop, we're done
            placeHasNext = false;
        }

        @Override
        public boolean hasNext() {
            return placeHasNext;
        }

        @Override
        public PlaceMove next() {
            //y++ so that we move to the next square (kinda icky I know)
            PlaceMove move = placeMove(x, y++, Stone.Type.values()[m]);

            //prepare the next move
            preparePlace();

            return move;
        }
    }

    /**
     * An iterator over all possible StackMoves given a Tak instance
     */
    private static class StackMoveIterator implements Iterator<StackMove> {
        Tak tak;

        // stack moves
        // d keeps track of the 4 directions
        // d = 4 init to trigger reset
        int d = Direction.values().length;

        // n keeps track of pickup val
        int n = 0;

        // StackValsIterator Iterator keeps track of ways to sum to n
        StackValsIterator vIter;

        // u,v keeps track of each square
        int u = 0;
        int v = 0;

        boolean stackHasNext;

        StackMoveIterator(Tak tak) {
            this.tak = new Tak(tak);
            prepareStack();
        }

        void prepareStack() {
            if (d == Direction.values().length) { // done with the current stack
                // find the next stack that the current player owns
                for (int i = u; i < tak.size(); i++) {
                    for (int j = v; j < tak.size(); j++) {
                        if (tak.getStackAt(i, j).peek().player == tak.getCurrentPlayerIndex()) {
                            u = i;
                            v = j;
                            d = 0;
                            n = 1;

                            vIter = new StackValsIterator(1, lengthToNearestStop(tak, u, v, Direction.values()[d]));
                            prepareStack(); // recurse to make sure pIter has next
                        }
                    }
                    v = 0;
                }

                // if we can't find a suitable stack, we're done
                stackHasNext = false;
            } else if (!vIter.hasNext()) {
                if (n < tak.getStackAt(u, v).size() && n < tak.size()) { // done with picking up n
                    n += 1;

                    Direction dir = Direction.values()[d];
                    int stop = lengthToNearestStop(tak, u, v, dir) + 1;

                    // if the top stone of the pickup is a cap stone, and
                    // we got stopped by a piece (not the end of the board), and
                    // we can reach the piece that stopped us, and
                    // the piece that stopped us is a standing stone
                    if (tak.getStackAt(u, v).peek().type.equals(Stone.Type.CAP)
                            && stop < tak.size() && n >= stop
                            && tak.getStackAt(u + stop * dir.dx, v + stop * dir.dy).peek().type.equals(Stone.Type.STANDING)) {
                        // special capstone iterator
                        vIter = new StackValsCapIterator(n, stop - 1);
                    } else {
                        vIter = new StackValsIterator(n, stop - 1);
                    }
                } else { // done with all pickups
                    d += 1; // move on to next direction
                }

                prepareStack(); //recurse to make sure pIter has next
            }

            stackHasNext = true;
        }

        @Override
        public boolean hasNext() {
            return stackHasNext;
        }

        @Override
        public StackMove next() {
            StackMove move = stackMove(u, v++, Direction.values()[d], n, vIter.next());
            prepareStack();
            return move;
        }
    }

    /**
     * Iterates through all possible splitting of a stack of size {@code n}
     * for splits ranging from {@code minSize} to {@code size} (inclusive).
     * (Default for {@code minSize} is 1.)
     */
    private static class StackValsIterator implements Iterator<int[]> {

        /**
         * Fixed stack, variable spaces.
         * Map of number of spaces --> List of all permutations
         */
        HashMap<Integer, List<int[]>> FSVS;
        Iterator<int[]> currIter = Collections.emptyIterator();

        int size;
        int minSize;
        int n;

        /**
         * Iterates through all possible splitting of a stack of size {@code n}
         * for splits ranging from {@code minSize} to {@code size} (inclusive).
         * (Default for {@code minSize} is 1.) Exactly the same as
         * {@code StackValsIterator(n, size, 1)}.
         */
        StackValsIterator(int n, int size) {
            this(n, size, 1);
        }

        /**
         * Iterates through all possible splitting of a stack of size {@code n}
         * for splits ranging from {@code minSize} to {@code size} (inclusive).
         */
        StackValsIterator(int n, int size, int minSize) {
            if (minSize <= 0) {
                throw new IllegalArgumentException("min size has to be at least 1");
            }

            if (!allStackVals.containsKey(n)) {
                allStackVals.put(n, new HashMap<>());
            }

            FSVS = allStackVals.get(n);
            this.n = n;
            this.size = size;
            this.minSize = minSize;
            prepareStackVals();
        }

        void prepareStackVals() {
            if (!currIter.hasNext() && minSize <= size) {
                if (FSVS.containsKey(minSize)) {
                    currIter = FSVS.get(minSize).iterator();
                } else {
                    List<int[]> partitions = new ArrayList<>();
                    FSVS.put(minSize, partitions);

                    //compute partitions adding to n using exactly minSize partitions
                    List<int[]> uniquePartitions = getPartitions(n, minSize, n - minSize + 1);
                    for (int[] vals : uniquePartitions) {
                        partitions.addAll(getPermutations(vals));
                    }
                }
                minSize += 1;
            }
        }

        /**
         * @param n The number to sum to
         * @param k The exact number of summands
         * @param m The maximum an element can be in the partition
         * @return A unique (up to rearrangement) list of all the partitions
         * that sum to n using k parts.
         */
        List<int[]> getPartitions(int n, int k, int m) {
            List<int[]> result = new ArrayList<>();
            if (k == 0) {
                if (n == 0) {
                    result.add(new int[0]);
                }
                return result;
            }

            for (int i = 1; i <= m; i++) {
                for (int[] vals : getPartitions(n - i, k - 1, i)) {
                    int[] temp = new int[vals.length + 1];
                    System.arraycopy(vals, 0, temp, 1, vals.length);
                    temp[0] = i;

                    result.add(temp);
                }
            }

            return result;
        }

        /**
         * @param vals The array to compute the permutations of
         * @return A list of all unique permutations of the given array
         */
        List<int[]> getPermutations(int[] vals) {
            List<int[]> result = new ArrayList<>();

            if (vals.length == 1) {
                result.add(vals);
                return result;
            }

            int[] temp = new int[vals.length - 1];
            System.arraycopy(vals, 1, temp, 0, temp.length);

            HashSet<int[]> unique = new HashSet<>();
            for (int[] myInts : getPermutations(temp)) {
                for (int i = 0; i < vals.length; i++) {
                    int[] foo = new int[vals.length];

                    System.arraycopy(myInts, 0, foo, 0, i);
                    foo[i] = vals[0];
                    System.arraycopy(myInts, i, foo, i + 1, vals.length - i - 1);

                    if (!unique.contains(foo)) {
                        result.add(foo);
                        unique.add(foo);
                    }
                }
            }
            return result;
        }

        @Override
        public boolean hasNext() {
            return currIter.hasNext();
        }

        @Override
        public int[] next() {
            int[] vals = currIter.next();
            prepareStackVals();
            return vals;
        }
    }

    /**
     * Special case of {@code StackValsIterator} with a cap stone flattening a standing stone.
     * Includes a regular stack iterator of everything up to but not including the
     * standing stone, plus a regular stack iterator of {@code minSize} size minus a piece so
     * we can append a 1 to resulting vals array to flatten the standing stone.
     */
    private static class StackValsCapIterator extends StackValsIterator {

        Iterator<int[]> specialIter = Collections.emptyIterator();

        StackValsCapIterator(int n, int size) {
            super(n, size);

            if (n > 1) {
                specialIter = new StackValsIterator(n - 1, size, size);
            }
        }

        @Override
        public boolean hasNext() {
            return super.hasNext() || specialIter.hasNext();
        }

        @Override
        public int[] next() {
            if (super.hasNext()) {
                return super.next();
            } else if (specialIter.hasNext()) {
                int[] tVals = specialIter.next();
                int[] vals = new int[tVals.length + 1];
                System.arraycopy(tVals, 0, vals, 0, tVals.length);

                vals[vals.length - 1] = 1;

                return vals;
            } else {
                throw new NoSuchElementException("No more items in the iterator");
            }
        }
    }
}
