package base.move;

import base.Stone;
import base.Tak;

public class PlaceMove extends Move {

    public Stone.Type type;

    public PlaceMove(int x, int y, int player, Stone.Type type) {
        super(x, y, player);
        this.type = type;
    }

    @Override
    public void action(Tak tak) {
        tak.getStackAt(x, y).addElement(new Stone(player, type));
        tak.getRoadGraph().updateVertex(x, y, player);
    }
}
