package engine.engines;

import base.Tak;
import base.move.Move;
import base.move.MoveFactory;
import engine.Engine;
import engine.models.Model;
import structures.MaxPQ;
import structures.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>MinMaxDepthEngine</code> class chooses the move which maximizes the evaluation of the
 * next position with the given <code>Model</code> up to a given depth.
 */
public class MinMaxDepthEngine implements Engine {

    private Model model;
    private int depth;
    private List<Tuple<Move,Double>> currentMoves;

    public MinMaxDepthEngine(Model model, int depth) {
        this.model = model;
        this.depth = depth;
    }

    @Override
    public void solve(Tak tak, long theirTime, long myTime) throws InterruptedException {
        currentMoves = new ArrayList<>();

        for (Move m : MoveFactory.allPossibleMoves(new Tak(tak))) {
            Tak takCopy = new Tak(tak);
            takCopy.executeMove(m);

            currentMoves.add(new Tuple<>(m, -1 * eval(takCopy, 1)));
        }
    }

    /**
     * @param tak The Tak instance to evaluate
     * @param d The current depth
     * @return A double, positive if the current player is winning, negative losing, or zero if tied.
     */
    private double eval(Tak tak, int d) {
        if (tak.isGameOver()) {
            if (tak.getGameResult().equals(Tak.GameResult.TIE)) {
                return 0.0;
            } else if (tak.getGameResult().equals(Tak.GameResult.WHITE) && tak.getCurrentPlayerIndex() == 0) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }

        if (d >= 2 * depth) {
            int minMaxInt = 1; // if current player is white, want to maximize
            if (tak.getCurrentPlayerIndex() == 1) { // if current player is black, want to minimize
                minMaxInt = -1;
            }

            return minMaxInt * model.evaluate(tak);
        }

        double bestVal = Double.NEGATIVE_INFINITY;
        for (Move m : MoveFactory.allPossibleMoves(new Tak(tak))) {
            Tak takCopy = new Tak(tak);
            takCopy.executeMove(m);

            double val = -1 * eval(takCopy, d + 1);
            if (val > bestVal) {
                bestVal = val;
            }
        }

        return bestVal;
    }

    @Override
    public List<Tuple<Move, Double>> pollMoves() {
        currentMoves.sort(MOVE_TUPLE_COMPARATOR.reversed());
        return currentMoves;
    }
}
