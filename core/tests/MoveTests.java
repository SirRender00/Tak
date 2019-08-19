import base.Tak;
import base.move.Move;
import base.move.MoveFactory;

public class MoveTests {

    public static void stackTest1() throws Tak.TakException {
        Tak tak = new Tak(Tak.GameType.FIVE);

        executeAndPrint(tak, MoveFactory.parseMove(tak, "a1"));
        executeAndPrint(tak, MoveFactory.parseMove(tak, "b2"));
        executeAndPrint(tak, MoveFactory.parseMove(tak, "1b2+1"));
    }

    public static void executeAndPrint(Tak tak, Move move) throws Tak.TakException {
        tak.safeExecuteMove(move);
        tak.printRoaGraph();
    }

    public static void main(String[] args) throws Tak.TakException {
        stackTest1();
    }
}
