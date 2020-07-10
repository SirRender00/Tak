package engine.models;

import base.Stack;
import base.Stone;
import base.Tak;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExperimentalHeuristic1 implements Model {

    @Override
    public double evaluate(Tak tak) {
        if (tak.isGameOver()) {
            if (tak.getGameResult().equals(Tak.GameResult.WHITE)) {
                return Double.POSITIVE_INFINITY;
            } else if (tak.getGameResult().equals(Tak.GameResult.BLACK)) {
                return Double.NEGATIVE_INFINITY;
            } else {
                return 0.0;
            }
        }

        return boardControl(tak) + totalRoads(tak);
    }

    public double totalRoads(Tak tak) {
        double whiteRoads = verticalRoads(tak, 0) + horizontalRoads(tak, 0);
        double blackRoads = horizontalRoads(tak, 1) + horizontalRoads(tak, 1);

        return whiteRoads - blackRoads;
    }

    public double verticalRoads(Tak tak, int player) {
        return roads(tak, player, true);
    }

    public double horizontalRoads(Tak tak, int player) {
        return roads(tak, player, false);
    }

    public double roads(Tak tak, int player, boolean vertical) {
        int[] row = new int[tak.boardSize()];
        int max = 0;

        for (int j = 0; j < tak.boardSize(); j++) {
            for (int i = 0; i < tak.boardSize(); i++) {
                Stone stone;
                if (vertical) {
                    stone = tak.getStackAt(i, j).peek();
                } else {
                    stone = tak.getStackAt(j, i).peek();
                }

                if (!stone.type.equals(Stone.Type.STANDING) && stone.player == player) {
                    row[i] += 1;
                } else {
                    row[i] = 0;
                }

                updateRow(tak, row);

                int temp = max(row);
                if (temp > max) {
                    max = temp;
                }
            }
        }

        return max;
    }

    private int max(int[] vals) {
        int best = Integer.MIN_VALUE;

        for (Integer n : vals) {
            if (n > best) {
                best = n;
            }
        }

        return best;
    }

    private void updateRow(Tak tak, int[] row) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < row.length; i++) {
            map.put(i, row[i]);
        }

        LinkedHashMap<Integer, Integer> sorted = new LinkedHashMap<>();
        map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

        for (Integer index : sorted.keySet()) {
            update(tak, row, index);
        }
    }

    private void update(Tak tak, int[] row, int index) {
        int max = 0;

        for (int i = index - 1; i <= index + 1; i++) {
            if (tak.inBounds(i) && row[i] > max) {
                max = row[i];
            }
        }

        row[index] = max;
    }

    /**
     * @param tak The current Tak position
     * @return A measure of current board control for white.
     */
    public double boardControl(Tak tak) {
        double pieceCount = 0;
        double decay = 0.45;

        for (int i = 0; i < tak.boardSize(); i++) {
            for (int j = 0; j < tak.boardSize(); j++) {
                Stack stack = tak.getStackAt(i, j);
                Iterator<Stone> stoneIterator = stack.stoneIterator(stack.size(), true);

                double d = 1;
                while(stoneIterator.hasNext()) {
                    Stone stone = stoneIterator.next();
                    if (stone.player == 0) {
                        pieceCount += d;
                    } else if (stone.player == 1) {
                        pieceCount -= d;
                    }

                    d *= decay;
                }
            }
        }

        return pieceCount;
    }
}
