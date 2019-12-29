package engine;

import base.Tak;
import base.move.Move;
import structures.Tuple;

import java.util.List;

public abstract class Model {

    public abstract void update(GameTree tree, double outcome);

    public abstract void train(GameTree tree);

    public abstract List<Tuple<Move, Double>> getMoves(Tak tak);

    public abstract double getValuation(Tak tak);
}
