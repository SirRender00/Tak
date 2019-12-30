package base;

/**
 * Core piece of the Tak game. Stones are owned by players
 * and can be of any one of the three {@link Type}: <code>FLAT</code>,
 * <code>STANDING</code>, or <code>CAP</code>.
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
     * @return A copy of this stone.
     */
    public Stone copy() {
        return new Stone(player, type);
    }

    /**
     * Make this stone of type <code>FLAT</code>.
     */
    public void flatten() {
        type = Type.FLAT;
    }

    public enum Type {
        FLAT, STANDING, CAP
    }
}
