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
                    move = MoveFactory.parseMove(msg);
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    continue;
                }

                try {
                    tak.safeExecuteMove(move);
                } catch (Tak.TakException e) {
                    System.out.println(e.getMessage());
                }

                System.out.println(tak.getRoadGraphString());
            }
        }

        System.out.println("Game over! " + tak.getGameResult().getMessage());
    }
}
