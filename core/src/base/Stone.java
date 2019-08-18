package base;

public class Stone {

    public Type type;
    public int player;

    public Stone(int player, Type type) {
        this.type = type;
        this.player = player;
    }

    public enum Type {
        FLAT, STANDING, CAP
    }
}
