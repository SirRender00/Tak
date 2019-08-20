package base;

/**
 * Core piece of the Tak game. Stones are owned by players
 * and can be any one of the three {@link Type}: FLAT, STANDING, or CAP.
 */
public class Stone {

    public Type type;
    public int player;

    /**
     * @param player The player that owns the stone
     * @param type The type of the stone
     */
    public Stone(int player, Type type) {
        this.type = type;
        this.player = player;
    }

    /**
     * A copy of a stone.
     * @param stone The stone to copy
     */
    public Stone(Stone stone) {
        this.type = stone.type;
        this.player = stone.player;
    }

    /**
     * Make this stone flat
     */
    public void flatten() {
        type = Type.FLAT;
    }

    public enum Type {
        FLAT, STANDING, CAP
    }
}
