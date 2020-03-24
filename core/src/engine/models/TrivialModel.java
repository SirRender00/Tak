package engine.models;

import base.Tak;

public class TrivialModel implements Model {

    @Override
    public double evaluate(Tak tak) {
        if (tak.isGameOver()) {
            if (tak.getGameResult().equals(Tak.GameResult.WHITE)) {
                return Double.POSITIVE_INFINITY;
            } else if (tak.getGameResult().equals(Tak.GameResult.BLACK)) {
                return Double.NEGATIVE_INFINITY;
            }
        }

        return 0.0;
    }
}
