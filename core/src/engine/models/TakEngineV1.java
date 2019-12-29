package engine.models;

import base.Tak;
import base.move.Move;
import engine.GameTree;
import engine.Model;
import structures.Tuple;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class TakEngineV1 extends Model {

    @Override
    public List<Tuple<Move, Double>> getMoves(Tak tak) {
        return null;
    }

    @Override
    public double getValuation(Tak tak) {
        return 0;
    }

    @Override
    public void update(GameTree tree, double outcome) {

    }

    @Override
    public void train(GameTree tree) {
        Tak tak = tree.tak;
        Stack<GameTree> stack = new Stack<>();
        stack.push(tree);

        while(!tak.isGameOver()) {
            tree = chooseBestTraining(tree.getChildren());
            stack.push(tree);
            tak = tree.tak;
        }

        while(!stack.isEmpty()) {
            GameTree gTree = stack.pop();

            gTree.timesVisited += 1;
            gTree.favorsWhite += tak.getGameResult().getWhitePayoff();

            update(gTree, tak.getGameResult().getWhitePayoff());
        }
    }

    private GameTree chooseBestTraining(Iterator<GameTree> trees) {
        GameTree best = null;
        double bestVal = Double.NEGATIVE_INFINITY;

        while (trees.hasNext()) {
            GameTree tree = trees.next();
            double val = getTrainingValuation(tree.tak);

            if (val > bestVal) {
                best = tree;
                bestVal = val;
            }
        }

        return best;
    }

    private GameTree chooseBest(Iterator<GameTree> trees) {
        GameTree best = trees.next();
        double bestVal = getValuation(best.tak);

        while (trees.hasNext()) {
            GameTree tree = trees.next();
            double val = getValuation(tree.tak);

            if (val > bestVal) {
                best = tree;
                bestVal = val;
            }
        }

        return best;
    }

    private double getTrainingValuation(Tak tak) {
        //TODO
        return getValuation(tak);
    }
}
