package engine.models;

import base.Tak;

/**
 * The <code>Model</code> class represents a way to evaluate a given Tak position.
 * It only has one method <code>evaluate(Tak)</code>.
 */
public interface Model {

    /**
     * @param tak The tak instance to evaluate.
     * @return A valuation of the current position (positive for white winning, 0 for tie).
     */
    double evaluate(Tak tak);
}
