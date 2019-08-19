package base.move;

import base.Stone;
import base.Tak;
import structures.Direction;

import java.util.Iterator;

public class MoveFactory {

    public static PlaceMove placeMove(int x, int y, Stone.Type type) {
        return new PlaceMove(x, y, type);
    }

    public static StackMove stackMove(int x, int y, Direction dir, int pickup, int[] vals) {
        return new StackMove(x, y, dir, pickup, vals);
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
                    charToRow(chars[2]),
                    charToDirection(chars[3]),
                    Character.getNumericValue(chars[0]),
                    vals);
        } else if (Character.isAlphabetic(chars[0])) {
            if (chars.length != 2) {
                throw new RuntimeException("Error in parsing move");
            }

            return placeMove(charToFile(chars[0]), charToRow(chars[1]), Stone.Type.FLAT);
        } else {
            if (chars.length >= 4) {
                throw new RuntimeException("Parse Error");
            }

            return new PlaceMove(
                    charToFile(chars[1]),
                    charToRow(chars[2]),
                    charToStoneType(chars[0]));
        }
    }

    public static int charToFile(char c) {
        return (int) c - 'a';
    }

    public static int charToRow(char c) {
        return Character.getNumericValue(c) - 1;
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

    /**
     * @param tak The tak instance to generate moves over.
     * @return An iterator over all the possible moves for the
     * current player.
     */
    public static Iterator<Move> allPossibleMoves(Tak tak) {
        //TODO

        return null;
    }
}
