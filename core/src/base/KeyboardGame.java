package base;

import base.move.MoveFactory;
import base.move.Move;

import java.util.Scanner;

public class KeyboardGame {

    public static void main(String[] args) {
        Tak tak = new Tak(Tak.GameType.FIVE);
        Scanner scanner = new Scanner(System.in);

        while (!tak.isGameOver()) {
            if (scanner.hasNextLine()) {
                String msg = scanner.nextLine();

                Move move;
                try {
                    move = MoveFactory.parseMove(tak, msg);
                } catch (Exception e) {
                    System.out.println("Error in parsing move");
                    continue;
                }

                try {
                    tak.safeExecuteMove(move);
                } catch (Tak.TakException e) {
                    System.out.println(e.getMessage());
                }

                tak.printRoaGraph();
            }
        }

        System.out.println("Game over! " + tak.getGameResult().getMessage());
    }
}
