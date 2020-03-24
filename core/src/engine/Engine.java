package engine;

import base.Tak;
import base.move.Move;
import structures.Tuple;

import java.util.Comparator;
import java.util.List;

/**
 * The <code>Engine</code> class represents a general AI for the game of Tak.
 * We interface with engines by first calling <code>solve(...)</code>
 * to set the position that the engine should solve. We can then poll the best moves
 * by calling <code>pollMoves()</code> which returns an ordered list of {@link Tuple}
 * objects with first element a {@link Move} and second element the valuation of the move.
 * This list is ordered best to worst move.
 */
public interface Engine {

    /**
     * Sets the current tak instance for the engine to solve. This allows one
     * to make calls to <code>pollMoves()</code> and <code>pollValuation()</code>
     * for the given tak position (which will change as we search longer).
     * @param tak The {@link Tak} instance to solve.
     * @param theirTime Amount of time the opponent has remaining (in milliseconds).
     * @param myTime Amount of time we have remaining (in milliseconds).
     */
    void solve(Tak tak, long theirTime, long myTime) throws InterruptedException;

    /**
     * Returns an ordered list of moves of the current position as set by calling
     * <code>solve(...)</code>. Ordered by best move to worst move.
     *
     * @return A List of {@link Tuple} objects with first element a {@link Move} object
     * and second element a valuation of how good the move is. The entire list should be
     * ordered by best move to worst move.
     */
    List<Tuple<Move, Double>> pollMoves();

    /**
     * Returns a valuation (of the best move) of the current position as set by calling
     * <code>solve(...)</code>.
     * <br>
     * Exactly equivalent to calling <code>pollMoves().get(0).two</code>.
     *
     * @return a valuation (of the best move) of the current position.
     */
    default double pollValuation() {
        return pollMoves().get(0).two;
    }

    MoveTupleComparator MOVE_TUPLE_COMPARATOR = new MoveTupleComparator();

    class MoveTupleComparator implements Comparator<Tuple<Move, Double>> {

        @Override
        public int compare(Tuple<Move, Double> o1, Tuple<Move, Double> o2) {
            double val = o1.two - o2.two;

            if (val > 0) {
                return 1;
            } else if (val < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
