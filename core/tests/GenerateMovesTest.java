import base.Stone;
import base.Tak;
import base.move.Move;
import base.move.MoveFactory;
import base.move.PlaceMove;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GenerateMovesTest {

    public static void executeConsecutiveMoves(Tak tak, String... sMoves) throws Tak.TakException {
        for (String str : sMoves) {
            tak.safeExecuteMove(MoveFactory.parseMove(str));
        }
    }

    @Test
    public void firstMoveGenTest()  {

        Tak tak = new Tak(Tak.GameType.FIVE);

        int flatCount = 0;
        int other = 0;
        for (Move m : MoveFactory.allPossibleMoves(tak)) {
            Assert.assertTrue(m instanceof PlaceMove);
            Assert.assertTrue(tak.validateMove(m));

            if (((PlaceMove) m).type.equals(Stone.Type.FLAT)) {
                flatCount += 1;
            } else {
                other += 1;
            }
        }

        Assert.assertEquals(25, flatCount);
        Assert.assertEquals(0, other);
    }

    @Test
    public void secondMoveGenTest() throws Tak.TakException {
        Tak tak = new Tak(Tak.GameType.FIVE);
        executeConsecutiveMoves(tak, "a1", "a2");

        int flatCount = 0;
        int standCount = 0;
        int capCount = 0;
        int stackCount = 0;

        for (Move m : MoveFactory.allPossibleMoves(tak)) {
            Assert.assertTrue(tak.validateMove(m));

            if (m instanceof PlaceMove) {
                if (((PlaceMove) m).type.equals(Stone.Type.FLAT)) {
                    flatCount += 1;
                } else if (((PlaceMove) m).type.equals(Stone.Type.STANDING)){
                    standCount += 1;
                } else {
                    capCount += 1;
                }
            } else {
                stackCount += 1;
            }
            System.out.println(m);
        }

        Assert.assertEquals(23, flatCount);
        Assert.assertEquals(23, standCount);
        Assert.assertEquals(23, capCount);
        Assert.assertEquals(3, stackCount);
    }
}
