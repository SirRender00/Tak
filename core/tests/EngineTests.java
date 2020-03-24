import base.Tak;
import base.move.MoveFactory;
import org.junit.Assert;
import org.junit.Test;

public class EngineTests {

    public static void executeConsecutiveMoves(Tak tak, String... sMoves) throws Tak.TakException {
        for (String str : sMoves) {
            tak.safeExecuteMove(MoveFactory.parseMove(str));
        }
    }
}
