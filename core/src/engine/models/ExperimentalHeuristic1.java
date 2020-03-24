package engine.models;

import base.Stack;
import base.Stone;
import base.Tak;

import java.util.Iterator;

import static structures.Utilities.max;
import static structures.Utilities.min;

public class ExperimentalHeuristic1 implements Model {

    @Override
    public double evaluate(Tak tak) {
        if (tak.isGameOver()) {
            if (tak.getGameResult().equals(Tak.GameResult.WHITE)) {
                return Double.POSITIVE_INFINITY;
            } else if (tak.getGameResult().equals(Tak.GameResult.BLACK)) {
                return Double.NEGATIVE_INFINITY;
            } else {
                return 0.0;
            }
        }

        return boardControl(tak);
    }

    /**
     * @param tak The current Tak position
     * @return A weighted sum of stack control
     */
    public double boardControl(Tak tak) {
        double pieceCount = 0;

        for (int i = 0; i < tak.boardSize(); i++) {
            for (int j = 0; j < tak.boardSize(); j++) {
                Stone stone = tak.getStackAt(i, j).peek();
                if (stone.player == 0) {
                    pieceCount += 1;
                } else if (stone.player == 1) {
                    pieceCount -= 1;
                }
            }
        }

        return pieceCount;
    }
}
