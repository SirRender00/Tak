package base.move;

import base.Stone;
import base.Tak;
import structures.Direction;

import java.util.Iterator;

/**
 * Pick ups a stack of size <code>pickup</code> at specified point, then
 * drops the bottom <code>vals[i]</code> one square at a time in Direction
 * <code>dir</code>, for i in length vals.
 */
public class StackMove extends Move {

    public Direction dir;
    public int[] vals;
    public int pickup;

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

        for (int i = 0; i < vals.length; i++) {
            transX += dir.dx;
            transY += dir.dy;

            for (int j = 0; j < vals[i]; j++) {
                tak.getStackAt(transX, transY).addElement(trans.next());
            }
            tak.getRoadGraph().updateVertex(transX, transY, tak.getStackAt(transX, transY).peek().player);
        }

        tak.getStackAt(transX, transY).peek().type = Stone.Type.FLAT;

        tak.getStackAt(x, y).removeRange(tak.getStackAt(x, y).tSize() - pickup, tak.getStackAt(x, y).tSize());
        tak.getRoadGraph().updateVertex(x, y, tak.getStackAt(x, y).peek().player);
    }
}
