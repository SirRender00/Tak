package base.move;

import base.Tak;

/**
 * Represents an abstract move in the game. Subclassed by PlaceMove
 * and StackMove. To execute on a tak instance, call <code>Tak.safeExecuteMove(this)</code>
 * or <code>Tak.executeMove(this)</code>.
 *
 */
public abstract class Move {

    public int x;
    public int y;

    /**
     * @param x The x coord of the move
     * @param y The y coord of the move
     */
    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This represents what the move would do ideally,
     * <em>without</em> checking for validity. <br><br>
     *
     * The proper way to execute this move is to call <code>Tak.safeExecuteMove(this)</code>
     * or <code>Tak.executeMove(this)</code>
     * @param tak The tak instance to execute this move on
     */
    public abstract void action(Tak tak);
}
