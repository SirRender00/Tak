import base.Stone;
import base.Tak;
import base.move.Move;
import base.move.MoveFactory;
import base.move.PlaceMove;
import base.move.StackMove;
import org.junit.Assert;
import org.junit.Test;


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
        }

        Assert.assertEquals(23, flatCount);
        Assert.assertEquals(23, standCount);
        Assert.assertEquals(23, capCount);
        Assert.assertEquals(3, stackCount);
    }

    @Test
    public void stackMoveGen1() throws Tak.TakException {
        // stacks 4 in the middle of the board WBWB, white should only
        // have place moves not concerning the middle
        // black should have the proper amount of stack moves
        Tak tak = new Tak(Tak.GameType.FIVE);
        executeConsecutiveMoves(tak, "c2", "b3", "d3", "c4");

        executeConsecutiveMoves(tak, "1b3>1", "1c2+1", "1d3<1", "1c4-1");

        for (Move m : MoveFactory.allPossibleMoves(tak)) {
            Assert.assertTrue(tak.validateMove(m));
            Assert.assertFalse(m instanceof StackMove);
            if (m instanceof PlaceMove) {
                Assert.assertFalse(m.x == 2 && m.y == 2);
            }
        }

        tak.safeExecuteMove(MoveFactory.parseMove("a1"));

        int stackCount = 0;
        for (Move m : MoveFactory.allPossibleMoves(tak)) {
            Assert.assertTrue(tak.validateMove(m));
            if (m instanceof PlaceMove) {
                Assert.assertFalse(m.x == 2 && m.y == 2);
            } else if (m instanceof StackMove) {
                stackCount += 1;
                System.out.println(m);
            }
        }

        Assert.assertEquals(40, stackCount);
    }

    @Test
    public void stackMoveGen2() throws Tak.TakException {
        // should not generate stack move onto capstone
        Tak tak = new Tak(Tak.GameType.FIVE);
        executeConsecutiveMoves(tak, "c2", "b3", "Cc1");

        for (Move m : MoveFactory.allPossibleMoves(tak)) {
            System.out.println(m);
            Assert.assertNotEquals("1c2-1", m.toString());
        }
    }

    @Test
    public void stackMoveGen3() throws Tak.TakException {
        // should be able to generate stack move onto standing stone with a capstone
        Tak tak = new Tak(Tak.GameType.FIVE);
        executeConsecutiveMoves(tak, "c2", "b3", "Cc1", "Sb1");

        boolean seen = false;
        for (Move m : MoveFactory.allPossibleMoves(tak)) {
            if (m.toString().equals("1c1<1")) {
                seen = true;
            }
        }

        System.out.println(tak.getRoadGraphString());
        Assert.assertTrue(seen);
    }

    @Test
    public void stackMoveGen4() throws Tak.TakException {
        // should not be able to generate stack move onto a standing stone
        Tak tak = new Tak(Tak.GameType.FIVE);
        executeConsecutiveMoves(tak, "c2", "b3", "Sb2", "c3");

        boolean seen = false;
        for (Move m : MoveFactory.allPossibleMoves(tak)) {
            if (m.toString().equals("1b3-1")) {
                seen = true;
            }
        }

        System.out.println(tak.getRoadGraphString());
        Assert.assertFalse(seen);
    }
}
