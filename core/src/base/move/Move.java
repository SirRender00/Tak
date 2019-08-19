package base.move;

import base.Tak;

public abstract class Move {

    public int x;
    public int y;

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This represents what the move would do ideally,
     * <em>without</em> checking for validity. <br><br>
     *
     * The proper way to execute this move is to call <code>Tak.executeMove(this)</code>
     * in the Tak class.
     * @param tak The tak instance to execute this move on
     */
    public abstract void action(Tak tak);
}
