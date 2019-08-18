package base;

import structures.Direction;
import base.move.Move;
import base.move.PlaceMove;
import base.move.StackMove;

import java.util.Scanner;

public class KeyboardGame {

    public static Tak tak;

    public static void main(String[] args) {
        tak = new Tak(Tak.GameType.FIVE);
        Scanner scanner = new Scanner(System.in);

        while (!tak.isGameOver()) {
            if (scanner.hasNextLine()) {
                Move move;
                try {
                    move = parseMove(scanner.nextLine());
                } catch (Exception e) {
                    System.out.println("Error in parsing move");
                    continue;
                }

                try {
                    tak.safeExecuteMove(move);
                } catch (Tak.TakException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        System.out.println("Game over! " + tak.getGameResult().getMessage());
    }

    public static Move parseMove(String str) {
        char[] chars = str.toCharArray();

        if (Character.isDigit(chars[0])) {
            int[] vals = new int[chars.length - 4];
            for (int i = 4; i < chars.length; i++) {
                if (!Character.isDigit(chars[i])) {
                    throw new RuntimeException("Parse Error");
                }

                vals[i - 4] = Character.getNumericValue(chars[i]);
            }

            return new StackMove(
                    charToFile(chars[1]),
                    Character.getNumericValue(chars[2]) - 1,
                    tak.getCurrentPlayer(),
                    charToDirection(chars[3]),
                    Character.getNumericValue(chars[0]),
                    vals);

        } else {
            if (chars.length >= 4) {
                throw new RuntimeException("Parse Error");
            }

            return new PlaceMove(
                    charToFile(chars[1]),
                    Character.getNumericValue(chars[2]) - 1,
                    tak.getCurrentPlayer(),
                    charToStoneType(chars[0]));
        }
    }

    public static int charToFile(char c) {
        switch (c) {
            case 'a': return 0;
            case 'b': return 1;
            case 'c': return 2;
            case 'd': return 3;
            case 'e': return 4;
            default: return -1;
        }
    }

    public static Direction charToDirection(char c) {
        if (c == '-') {
            return Direction.DOWN;
        } else if (c == '+') {
            return Direction.UP;
        } else if (c == '<') {
            return Direction.LEFT;
        } else if (c == '>') {
            return Direction.RIGHT;
        } else {
            return null;
        }
    }

    public static Stone.Type charToStoneType(char c) {
        if (c == 'F') {
            return Stone.Type.FLAT;
        } else if (c == 'S') {
            return Stone.Type.STANDING;
        } else if (c == 'C') {
            return Stone.Type.CAP;
        } else {
            return null;
        }
    }
}
