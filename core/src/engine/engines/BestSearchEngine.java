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
public class BestSearchEngine implements Engine {

    private Model model;
    private MaxPQ<Tuple<Move, Double>> moveQueue;

    public BestSearchEngine(Model model) {
        this.model = model;
    }

    @Override
    public void solve(Tak tak, long theirTime, long myTime) throws InterruptedException {

    }

    @Override
    public List<Tuple<Move, Double>> pollMoves() {
        return null;
    }
}
