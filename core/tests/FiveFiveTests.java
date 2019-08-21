import base.Tak;
import base.move.MoveFactory;
import org.junit.Assert;
import org.junit.Test;

public class FiveFiveTests {

    public static void executeConsecutiveMoves(Tak tak, String... sMoves) throws Tak.TakException {
        for (String str : sMoves) {
            tak.safeExecuteMove(MoveFactory.parseMove(str));
        }
    }

    @Test
    public void placeCollisionTest1() throws Tak.TakException {
        // cannot place on another piece
        Tak tak = new Tak(Tak.GameType.FIVE);

        tak.safeExecuteMove(MoveFactory.parseMove("a1"));
        Assert.assertFalse(tak.validateMove(MoveFactory.parseMove("a1")));
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void placeCollisionTest2() throws Tak.TakException {
        // moving a stone removes it from the stack
        // to be able to place another piece on
        Tak tak = new Tak(Tak.GameType.FIVE);

        executeConsecutiveMoves(tak, "a2", "a1", "1a1>1");

        Assert.assertTrue(tak.getStackAt(0, 0).isEmpty());
        Assert.assertTrue(tak.validateMove(MoveFactory.parseMove("a1")));
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void placeCollisionTest3() throws Tak.TakException {
        // Trying to move a stone onto a standing stone is not allowed
        Tak tak = new Tak(Tak.GameType.FIVE);

        executeConsecutiveMoves(tak, "a2", "a1", "Sb2");

        Assert.assertFalse(tak.validateMove(MoveFactory.parseMove("1a2>1")));
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void currentPlayerTest1() throws Tak.TakException {
        //whose turn it is
        Tak tak = new Tak(Tak.GameType.FIVE);

        Assert.assertEquals(0, tak.getCurrentPlayerIndex());
        tak.safeExecuteMove(MoveFactory.parseMove("a1"));

        Assert.assertEquals(1, tak.getCurrentPlayerIndex());
        tak.safeExecuteMove(MoveFactory.parseMove("a2"));

        Assert.assertEquals(0, tak.getCurrentPlayerIndex());
        tak.safeExecuteMove(MoveFactory.parseMove("a3"));

        Assert.assertEquals(1, tak.getCurrentPlayerIndex());
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void stonePlayerTest1() throws Tak.TakException {
        //who owns the stone for the first few moves
        Tak tak = new Tak(Tak.GameType.FIVE);

        Assert.assertEquals(1, tak.getStonePlayer());
        tak.safeExecuteMove(MoveFactory.parseMove("a1"));

        Assert.assertEquals(0, tak.getStonePlayer());
        tak.safeExecuteMove(MoveFactory.parseMove("a2"));

        Assert.assertEquals(0, tak.getStonePlayer());
        tak.safeExecuteMove(MoveFactory.parseMove("a3"));

        Assert.assertEquals(1, tak.getStonePlayer());
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void roadWinTest1() throws Tak.TakException {
        // Simple road connection by placement
        Tak tak = new Tak(Tak.GameType.FIVE);

        executeConsecutiveMoves(tak, "b1", "a1", "a2", "b2", "a3", "b3", "a4", "b4");
        Assert.assertFalse(tak.isGameOver());

        tak.safeExecuteMove(MoveFactory.parseMove("a5"));
        Assert.assertTrue(tak.isGameOver());
        Assert.assertEquals(tak.getGameResult(), Tak.GameResult.WHITE);
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void roadWinTest2() throws Tak.TakException {
        // Simple road connection by placement for black
        Tak tak = new Tak(Tak.GameType.FIVE);

        executeConsecutiveMoves(tak, "b1", "a1", "a2", "b2", "a3", "b3", "a4", "b4", "e1");
        Assert.assertFalse(tak.isGameOver());

        tak.safeExecuteMove(MoveFactory.parseMove("b5"));
        Assert.assertTrue(tak.isGameOver());
        Assert.assertEquals(Tak.GameResult.BLACK, tak.getGameResult());
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void roadWinTest3() throws Tak.TakException {
        // Moving a stack to make a win for yourself

        Tak tak = new Tak(Tak.GameType.FIVE);

        executeConsecutiveMoves(tak, "b1", "a1", "a2", "b2", "a3", "b3", "a4", "b4", "b5", "c1");
        Assert.assertFalse(tak.isGameOver());

        tak.safeExecuteMove(MoveFactory.parseMove("1b5<1"));
        Assert.assertTrue(tak.isGameOver());
        Assert.assertEquals(Tak.GameResult.WHITE, tak.getGameResult());
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void roadWinTest4() throws Tak.TakException {
        // Moving a stack to make a win for other player
        Tak tak = new Tak(Tak.GameType.FIVE);

        executeConsecutiveMoves(tak, "c1", "a1", "a2", "c2", "a3", "c3", "a4", "c4",
                "e5", "d5", "1e5<1", "e1");
        Assert.assertFalse(tak.isGameOver());

        tak.safeExecuteMove(MoveFactory.parseMove("2d5<11"));
        Assert.assertTrue(tak.isGameOver());
        Assert.assertEquals(Tak.GameResult.BLACK, tak.getGameResult());
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void roadWinTest5() throws Tak.TakException {
        // simultaneous win for both, results in giving to the current player
        Tak tak = new Tak(Tak.GameType.FIVE);

        executeConsecutiveMoves(tak, "b1", "a1", "a2", "b2", "a3", "b3", "a4", "b4",
                "b5", "c5", "1b5>1", "e1");
        Assert.assertFalse(tak.isGameOver());

        tak.safeExecuteMove(MoveFactory.parseMove("2c5<11"));
        Assert.assertTrue(tak.isGameOver());
        Assert.assertEquals(Tak.GameResult.WHITE, tak.getGameResult());
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void flatStoneWin1() throws Tak.TakException {
        // Fill the board and win for white

        Tak tak = new Tak(Tak.GameType.FIVE);
        MoveFactory.PlaceMoveIterator pIter = new MoveFactory.PlaceMoveIterator(tak);

        for (int i = 0; i < 24; i++) {
            tak.safeExecuteMove(pIter.next());
        }
        Assert.assertFalse(tak.isGameOver());

        tak.safeExecuteMove(MoveFactory.parseMove("e5"));
        Assert.assertTrue(tak.isGameOver());
        Assert.assertEquals(Tak.GameResult.WHITE, tak.getGameResult());
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void flatStoneWin2() throws Tak.TakException {
        // Fill the board and win for black

        Tak tak = new Tak(Tak.GameType.FIVE);
        MoveFactory.PlaceMoveIterator pIter = new MoveFactory.PlaceMoveIterator(tak);

        for (int i = 0; i < 22; i++) {
            tak.safeExecuteMove(pIter.next());
        }
        Assert.assertFalse(tak.isGameOver());

        executeConsecutiveMoves(tak, "Se3", "e4", "Se5");
        Assert.assertTrue(tak.isGameOver());
        Assert.assertEquals(Tak.GameResult.BLACK, tak.getGameResult());
        System.out.println(tak.getRoadGraphString());
    }

    @Test
    public void flatStoneWin3() throws Tak.TakException {
        // Fill the board and tie
        Tak tak = new Tak(Tak.GameType.FIVE);

        MoveFactory.PlaceMoveIterator pIter = new MoveFactory.PlaceMoveIterator(tak);

        for (int i = 0; i < 24; i++) {
            tak.safeExecuteMove(pIter.next());
        }
        Assert.assertFalse(tak.isGameOver());

        tak.safeExecuteMove(MoveFactory.parseMove("Se5"));
        Assert.assertTrue(tak.isGameOver());
        Assert.assertEquals(Tak.GameResult.TIE, tak.getGameResult());
        System.out.println(tak.getRoadGraphString());
    }

    // TODO: Make stack move tests
}
