package base.move;

import base.Stone;
import base.Tak;

/**
 * Places a stone of specified type, belonging to the specified player
 * at the specified point.
 */
public class PlaceMove extends Move {

    public Stone.Type type;

    public PlaceMove(int x, int y, Stone.Type type) {
        super(x, y);
        this.type = type;
    }

    @Override
    public void action(Tak tak) {
        tak.getStackAt(x, y).addElement(new Stone(tak.getCurrentPlayer(), type));
        tak.getRoadGraph().updateVertex(x, y, tak.getCurrentPlayer());
    }
}
