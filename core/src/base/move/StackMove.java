package base.move;

import base.Stone;
import base.Tak;
import structures.Direction;

import java.util.Iterator;

public class StackMove extends Move {

    public Direction dir;
    public int[] vals;
    public int pickup;

    public StackMove(int x, int y, int player, Direction dir, int pickup, int[] vals) {
        super(x, y, player);
        this.dir = dir;
        this.vals = vals;
        this.pickup = pickup;
    }

    @Override
    public void action(Tak tak) {
        Iterator<Stone> trans = tak.getStackAt(x, y).stoneIterator(pickup);

        for (int i = 0; i < vals.length - 1; i++) {
            x += dir.dx;
            y += dir.dy;

            for (int j = 0; j < vals[i]; j++) {
                tak.getStackAt(x, y).addElement(trans.next());
            }
            tak.getRoadGraph().updateVertex(x, y, tak.getStackAt(x, y).peek().player);
        }

        x += dir.dx;
        y += dir.dy;

        tak.getStackAt(x, y).peek().type = Stone.Type.FLAT;
        for (int j = 0; j < vals[vals.length - 1]; j++) {
            tak.getStackAt(x, y).addElement(trans.next());
        }
        tak.getRoadGraph().updateVertex(x, y, tak.getStackAt(x, y).peek().player);
    }
}
