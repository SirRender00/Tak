package base.move;

import base.Stone;
import base.Tak;
import structures.Direction;

import java.util.Iterator;

/**
 * Pick ups a stack of size <code>pickup</code> at specified point, then
 * drops the bottom <code>vals[i]</code> one square at a time in {@link Direction}
 * <code>dir</code>, for i in length vals.
 */
public class StackMove extends Move {

    public Direction dir;
    public int[] vals;
    public int pickup;

    /**
     * @param x The x coord to pickup stones
     * @param y The y coord to pickup stones
     * @param dir The direction to drop stones
     * @param pickup The amount to pick up
     * @param vals The amount to drop down at each step, going on for <code>vals.length</code>
     */
    public StackMove(int x, int y, Direction dir, int pickup, int[] vals) {
        super(x, y);
        this.dir = dir;
        this.vals = vals;
        this.pickup = pickup;
    }

    @Override
    public void action(Tak tak) {
        Iterator<Stone> trans = tak.getStackAt(x, y).stoneIterator(pickup);
        int transX = x;
        int transY = y;

        for (int val : vals) {
            transX += dir.dx;
            transY += dir.dy;

            for (int j = 0; j < val; j++) {
                tak.getStackAt(transX, transY).addElement(trans.next());
            }

            tak.updateRoadGraph(transX, transY);
        }

        tak.getStackAt(transX, transY).peek().flatten();

        tak.getStackAt(x, y).removeRange(tak.getStackAt(x, y).tSize() - pickup, tak.getStackAt(x, y).tSize());
        tak.updateRoadGraph(x, y);
    }
}
