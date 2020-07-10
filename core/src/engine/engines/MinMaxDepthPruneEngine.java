package engine.engines;

import base.Tak;
import base.move.Move;
import base.move.MoveFactory;
import engine.Engine;
import engine.models.Model;
import structures.Tuple;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.max;
import static java.lang.Double.min;

/**
 * The <code>MinMaxDepthEngine</code> class chooses the move which maximizes the evaluation of the
 * next position with the given <code>Model</code> up to a given depth.
 */
public class MinMaxDepthPruneEngine implements Engine {

    private Model model;
    private int depth;
    private List<Tuple<Move,Double>> currentMoves;

    public MinMaxDepthPruneEngine(Model model, int depth) {
        this.model = model;
        this.depth = depth;
    }

    @Override
    public void solve(Tak tak, long theirTime, long myTime) throws InterruptedException {
        currentMoves = new ArrayList<>();

        if (tak.isGameOver()) {
            return;
        }

        currentMoves.add(eval(tak, 2 * depth, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, tak.getCurrentPlayerIndex() == 0));
    }

    private Tuple<Move, Double> eval(Tak tak, int d, double alpha, double beta, boolean white) {
        if (d == 0 || tak.isGameOver()) {
            return new Tuple<>(null, model.evaluate(tak));
        }

        if (white) {
            double val = Double.NEGATIVE_INFINITY;
            Move move = null;

            for (Move m : MoveFactory.allPossibleMoves(new Tak(tak))) {
                if (move == null) {
                    move = m;
                }

                Tak takCopy = new Tak(tak);
                takCopy.executeMove(m);

                double temp = eval(takCopy, d - 1, alpha, beta, false).two;
                if (temp > val) {
                    val = temp;
                    move = m;
                }

                alpha = max(alpha, val);
                if (alpha >= beta) {
                    break;
                }
            }

            return new Tuple<>(move, val);
        } else {
            double val = Double.POSITIVE_INFINITY;
            Move move = null;

            for (Move m : MoveFactory.allPossibleMoves(new Tak(tak))) {
                if (move == null) {
                    move = m;
                }

                Tak takCopy = new Tak(tak);
                takCopy.executeMove(m);

                double temp = eval(takCopy, d - 1, alpha, beta, true).two;
                if (temp < val) {
                    val = temp;
                    move = m;
                }

                beta = min(beta, val);
                if (alpha >= beta) {
                    break;
                }
            }

            return new Tuple<>(move, val);
        }
    }

    @Override
    public List<Tuple<Move, Double>> pollMoves() {
        return currentMoves;
    }
}
