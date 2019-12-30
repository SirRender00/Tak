package base.move;

import base.Stone;
import base.Tak;
import structures.Direction;

import java.util.*;

/**
 * Utility functions related to the generation of Tak {@link Move} objects.
 * Includes creation of {@link PlaceMove} moves, {@link StackMove} moves,
 * parsing moves from a string by PTN convention
 * <a href="https://www.reddit.com/r/Tak/wiki/portable_tak_notation">Portable Tak Notation</a>,
 * and iterating over all possible moves for the current player given a Tak instance.
 */
public final class MoveFactory {

    private MoveFactory() {

    }

    /**
     * A map of [number of pieces --> a map of (number of spaces --> list of all permutations)]
     * Used so we don't have to compute stack permutations all the time.
     */
    private static Map<Integer, Map<Integer, List<int[]>>> allStackVals = new HashMap<>();

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
     * <a href="https://www.reddit.com/r/Tak/wiki/portable_tak_notation">Portable Tak Notation</a>.
     * @param str The string to parse in PTN notation.
     * @return The associated {@code Move} object.
     * @throws IllegalArgumentException if the string given is not a valid move.
     */
    public static Move parseMove(String str) {
        char[] chars = str.toCharArray();

        if (Character.isDigit(chars[0])) { // stack move
            if (chars.length <= 4) {
                throw new IllegalArgumentException("Error in parsing move.");
            }

            //set drop-down values
            int[] vals = new int[chars.length - 4];
            for (int i = 4; i < chars.length; i++) {
                if (!Character.isDigit(chars[i])) {
                    throw new IllegalArgumentException("Error in parsing move.");
                }

                vals[i - 4] = Character.getNumericValue(chars[i]);
            }

            return new StackMove(
                    charToFile(chars[1]),
                    charToRow(chars[2]),
                    charToDirection(chars[3]),
                    Character.getNumericValue(chars[0]),
                    vals);
        } else if (Character.isLowerCase(chars[0])) { // implicit flat stone place
            if (chars.length != 2) {
                throw new IllegalArgumentException("Error in parsing move.");
            }

            return placeMove(
                    charToFile(chars[0]),
                    charToRow(chars[1]),
                    Stone.Type.FLAT);
        } else if (Character.isAlphabetic(chars[0])) { // regular stone place
            if (chars.length >= 4) {
                throw new IllegalArgumentException("Error in parsing move.");
            }

            return placeMove(
                    charToFile(chars[1]),
                    charToRow(chars[2]),
                    charToStoneType(chars[0]));
        } else {
            throw new IllegalArgumentException("Error in parsing move.");
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

    public static char stoneTypeToChar(Stone.Type type) {
        if (type.equals(Stone.Type.FLAT)) {
            return 'F';
        }  else if (type.equals(Stone.Type.STANDING)) {
            return 'S';
        } else if (type.equals(Stone.Type.CAP)) {
            return 'C';
        } else {
            return '\u0000';
        }
    }

    public static char fileToChar(int n) {
        return (char) (n + 'a');
    }

    public static char directionToChar(Direction dir) {
        if (dir.equals(Direction.DOWN)) {
            return '-';
        } else if (dir.equals(Direction.UP)) {
            return '+';
        } else if (dir.equals(Direction.LEFT)) {
            return '<';
        } else if (dir.equals(Direction.RIGHT)) {
            return '>';
        } else {
            return '\u0000';
        }
    }

    /**
     * @param m The move to translate to string
     * @return The string representation of the given move.
     */
    public static String moveToString(Move m) {
        StringBuilder sMove = new StringBuilder();
        sMove.append(fileToChar(m.x));
        sMove.append(m.y + 1);

        if (m instanceof PlaceMove) {
            if (!((PlaceMove) m).type.equals(Stone.Type.FLAT)) { // implicit flat stone convention
                sMove.insert(0, stoneTypeToChar(((PlaceMove) m).type));
            }
        } else {
            StackMove stackMove = (StackMove) m;
            char cDir = directionToChar(stackMove.dir);

            sMove.insert(0, stackMove.pickup);
            for (int i = 0; i < stackMove.vals.length; i++) {
                sMove.append(cDir).append(stackMove.vals[i]);
            }
        }

        return sMove.toString();
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
     * @param x The x coordinate
     * @param y The y coordinate
     * @param dir The Direction to probe in
     * @return The number of squares from (x,y) in the specified direction
     * in between a non-flat stone or the boundary of the tak board.
     * If x,y is on an edge -- pointing off the board -- then this function returns 0.
     * If x,y is out of bounds then this function has undefined behavior.
     */
    private static int lengthToNearestStop(Tak tak, int x, int y, Direction dir) {
        for (int i = 0; i < tak.boardSize(); i++) {
            x += dir.dx;
            y += dir.dy;

            if (!tak.inBounds(x, y) ||
                    !tak.getStackAt(x, y).peek().type.equals(Stone.Type.FLAT)) {
                return i;
            }
        }

        return tak.boardSize() - 1;
    }

    /**
     * A combination of {@code PlaceMoveIterator} and {@code StackMoveIterator}.
     * Represents the iterator over all possible moves given a
     * certain Tak instance.
     */
    public static class MoveIterator implements Iterator<Move>, Iterable<Move> {

        StackMoveIterator sIter;
        PlaceMoveIterator pIter;
        Tak tak;

        MoveIterator(Tak tak) {
            this.tak = tak;
            tak.lock(); // We lock the tak instance to prevent concurrent modification.

            pIter = new PlaceMoveIterator(tak);
            sIter = new StackMoveIterator(tak);
        }

        @Override
        public Iterator<Move> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            if (pIter.hasNext() || sIter.hasNext()) {
                return true;
            } else {
                tak.unlock(); // Free the instance after.
                return false;
            }
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

        // x,y keeps track of current square
        int x;
        int y;

        // keep tracks of type of stones
        List<Stone.Type> posStones;
        int m;

        boolean placeHasNext;

        public PlaceMoveIterator(Tak tak) {
            this.tak = tak;

            // find the available stones for the current player
            posStones = new ArrayList<>(Stone.Type.values().length);

            if (tak.isFirstMove()) {
                posStones.add(Stone.Type.FLAT);
            } else {
                for (int u = 0; u < Stone.Type.values().length; u++) {
                    if (tak.getCurrentPlayer().getRemainingStones(Stone.Type.values()[u]) > 0) {
                        posStones.add(Stone.Type.values()[u]);
                    }
                }
            }

            // see prepareNextPlace() and next() to see why we initialize like this
            x = 0;
            y = -1;
            m = posStones.size();

            prepareNextPlaceMove();
        }

        void prepareNextPlaceMove() {
            if (m < posStones.size()) { // not done with the current empty square.
                return;
            }

            // find a new square starting with stone 0, and making sure we move on
            // by incrementing y
            m = 0;
            y += 1;
            for (int i = x; i < tak.boardSize(); i++) {
                for (int j = y; j < tak.boardSize(); j++) {
                    if (tak.getStackAt(i, j).isEmpty()) {
                        x = i;
                        y = j;
                        placeHasNext = true;
                        return;
                    }
                }
                y = 0;
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
            // m++ so that we move to the next stone
            PlaceMove move = placeMove(x, y, posStones.get(m));
            m += 1;

            prepareNextPlaceMove();

            return move;
        }
    }

    /**
     * An iterator over all possible StackMoves given a Tak instance
     */
    private static class StackMoveIterator implements Iterator<StackMove> {
        Tak tak;

        // d keeps track of the 4 directions
        int d;

        // n keeps track of pickup val
        int n;

        // StackValsIterator Iterator keeps track of ways to sum to n
        StackValsIterator vIter;

        // u,v keeps track of each square
        int u;
        int v;

        boolean stackHasNext;

        StackMoveIterator(Tak tak) {
            this.tak = tak;

            // init to trigger reset
            d = Direction.values().length - 1;

            // see findNewStack()
            u = 0;
            v = -1;

            n = 0;
            vIter = new StackValsIterator(0, 0, 0);

            prepareStack();
        }

        void prepareStack() {
            while (!vIter.hasNext()) {
                if (vIter.spaces == 0 // boxed in for this direction
                        || n >= tak.getStackAt(u, v).size() || n >= tak.boardSize()) { // done with all pickups
                    d += 1; // move on to next direction
                    n = 1;
                } else { // done with picking up n
                    n += 1;

                    Direction dir = Direction.values()[d];
                    int stop = lengthToNearestStop(tak, u, v, dir);

                    // if the top stone of the pickup is a cap stone, and
                    // we got stopped by a piece (not the end of the board), and
                    // we can reach the piece that stopped us, and
                    // the piece that stopped us is a standing stone
                    if (tak.getStackAt(u, v).peek().type.equals(Stone.Type.CAP)
                            && stop + 1 < tak.boardSize()
                            && n >= stop + 1
                            && tak.getStackAt(u + (stop + 1) * dir.dx, v + (stop + 1) * dir.dy)
                            .peek().type.equals(Stone.Type.STANDING)) {
                        // special capstone iterator
                        vIter = new StackValsCapIterator(n, stop);
                    } else {
                        vIter = new StackValsIterator(n, stop);
                    }
                    continue;
                }

                if (d == Direction.values().length) {
                    if (!findNextStack()) {
                        stackHasNext = false;
                        return;
                    }

                    d = 0;
                    n = 1;
                }

                vIter = new StackValsIterator(n, lengthToNearestStop(tak, u, v, Direction.values()[d]));
            }
            stackHasNext = true;
        }

        /**
         * Sets u,v to the new stack if found. (starting from the old u,v)
         * @return True if we were able to find a current-player-owned stack, false otherwise.
         */
        boolean findNextStack() {
            for (int i = u; i < tak.boardSize(); i++) {
                v += 1; // increment v to keep on moving
                for (int j = v; j < tak.boardSize(); j++) {
                    if (tak.getStackAt(i, j).peek().player == tak.getCurrentPlayerIndex()) {
                        u = i;
                        v = j;
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public boolean hasNext() {
            return stackHasNext;
        }

        @Override
        public StackMove next() {
            StackMove move = stackMove(u, v, Direction.values()[d], n, vIter.next());
            prepareStack();
            return move;
        }
    }

    /**
     * Iterates through all possible splitting of a stack of boardSize {@code n}
     * for splits ranging from {@code minSize} to {@code boardSize} (inclusive).
     * (Default for {@code minSize} is 1.)
     */
    private static class StackValsIterator implements Iterator<int[]> {

        /**
         * Fixed stack, variable spaces.
         * Map of number of spaces --> List of all permutations
         */
        Map<Integer, List<int[]>> FSVS;
        Iterator<int[]> currIter = Collections.emptyIterator();

        int spaces;
        int minSpaces;
        int n;

        /**
         * Iterates through all possible splitting of a stack of boardSize {@code n}
         * for splits ranging from {@code minSpaces} to {@code spaces} (inclusive).
         * (Default for {@code minSpaces} is 1.) This constructor is exactly the same
         * as calling {@code StackValsIterator(n, 1, spaces)}.
         *
         * @param n The stack pickup amount
         * @param spaces The max amount of spaces to partition up to
         */
        StackValsIterator(int n, int spaces) {
            this(n, 1, spaces);
        }

        /**
         * Iterates through all possible splitting of a stack of boardSize {@code n}
         * for splits ranging from {@code minSpaces} to {@code spaces} (inclusive).
         *
         * @param n The stack pickup amount
         * @param minSpaces The min amount of spaces to partition from
         * @param spaces The max amount of spaces to partition up to
         */
        StackValsIterator(int n, int minSpaces, int spaces) {
            this.n = n;
            this.spaces = spaces;
            this.minSpaces = minSpaces;

            if (n == 0) {
                return;
            }

            if (!allStackVals.containsKey(n)) {
                allStackVals.put(n, new HashMap<>());
            }

            FSVS = allStackVals.get(n);

            prepareStackVals();
        }

        void prepareStackVals() {
            if (!currIter.hasNext() && minSpaces <= spaces && minSpaces <= n) {
                if (!FSVS.containsKey(minSpaces)) {
                    ArrayList<int[]> partitions = new ArrayList<>();
                    FSVS.put(minSpaces, partitions);

                    Set<Integer> alreadySeen = new HashSet<>(); // hashcode of arrays computed differently
                    // compute partitions adding to n using exactly minSize partitions
                    List<int[]> uniquePartitions = getPartitions(n, minSpaces, n - minSpaces + 1);
                    for (int[] vals : uniquePartitions) {
                        for (int[] permutation : getPermutations(vals)) {
                            if (!alreadySeen.contains(Arrays.hashCode(permutation))) {
                                partitions.add(permutation);
                                alreadySeen.add(Arrays.hashCode(permutation));
                            }
                        }
                    }

                    partitions.trimToSize(); // save memory
                }

                currIter = FSVS.get(minSpaces).iterator();
                minSpaces += 1;
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

            // only one way to permute 1 element
            if (vals.length == 1) {
                result.add(vals);
                return result;
            }

            // We take out the first element and then consider
            // every permutation of the remaining elements.
            // We then place the first element in every position
            // ranging from 1 to length of the original list
            // for every such permutation.
            int[] temp = new int[vals.length - 1];
            System.arraycopy(vals, 1, temp, 0, temp.length);

            HashSet<int[]> unique = new HashSet<>();
            for (int[] myInts : getPermutations(temp)) {
                for (int i = 0; i < vals.length; i++) {
                    int[] foo = new int[vals.length];

                    System.arraycopy(myInts, 0, foo, 0, i);
                    foo[i] = vals[0];
                    System.arraycopy(myInts, i, foo, i + 1, vals.length - i - 1);

                    unique.add(foo);
                }
            }

            result.addAll(unique);
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
     * standing stone, plus a regular stack iterator of {@code minSize} boardSize minus a piece so
     * we can append a 1 to resulting vals array to flatten the standing stone.
     */
    private static class StackValsCapIterator extends StackValsIterator {

        Iterator<int[]> specialIter = Collections.emptyIterator();

        StackValsCapIterator(int n, int space) {
            super(n, space); // the regular iterator

            if (space == 0) { // we can only move the cap stone to flatten the stone
                specialIter = new StackValsIterator(1, 1);
            } else if (n > 1) {
                specialIter = new StackValsIterator(n - 1, space, space);
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
