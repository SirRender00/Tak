package engine;

import base.Tak;
import base.move.Move;
import structures.Tuple;

import java.util.List;

public interface Engine {

    List<Tuple<Move, Double>> getMoves(Tak tak);

    default double getValuation(Tak tak) {
        return getMoves(tak).get(0).two;
    }
}
