package engine;

import base.Tak;
import base.move.Move;
import structures.Tuple;

import java.util.List;

/**
 * The <code>Engine</code> class represents a general AI for the game of Tak.
 */
public interface Engine {

    /**
     * @param tak The tak instance to solve.
     * @param timeout The maximum amount of time to give for this computation.
     */
    void solvePosition(Tak tak, long timeout);

    List<Tuple<Move, Double>> pollMoves();

    double pollValuation();
}
