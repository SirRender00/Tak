package base.move;

import base.Stone;
import base.Tak;

/**
 * Places a {@link Stone} of specified type, belonging to the specified player
 * at the specified point.
 */
public class PlaceMove extends Move {

    public Stone.Type type;

    /**
     * @param x The x coord of the move
     * @param y The y coord of the move
     * @param type The type of stone to place
     */
    public PlaceMove(int x, int y, Stone.Type type) {
        super(x, y);
        this.type = type;
    }

    @Override
    public void action(Tak tak) {
        tak.getStackAt(x, y).addElement(new Stone(tak.getStonePlayer(), type));
        if (type == Stone.Type.FLAT || type == Stone.Type.STANDING) {
            tak.getCurrentPlayer().sideStones -= 1;
        } else {
            tak.getCurrentPlayer().capStones -= 1;
        }

        tak.updateRoadGraph(x, y);
    }
}
