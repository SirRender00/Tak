package engine;

import base.Tak;
import base.move.Move;
import base.move.MoveFactory;
import engine.engines.MinMaxDepthPruneEngine;
import engine.models.ExperimentalHeuristic1;
import structures.Tuple;

import java.util.Scanner;

public class EngineGame {

    public static void executeConsecutiveMoves(Tak tak, String... sMoves) throws Tak.TakException {
        for (String str : sMoves) {
            tak.safeExecuteMove(MoveFactory.parseMove(str));
        }
    }

    public static void main(String[] args) throws Tak.TakException {
        Tak tak = new Tak(Tak.GameType.SIX);

        Engine engine = new MinMaxDepthPruneEngine(new ExperimentalHeuristic1(), 2);

        Scanner scanner = new Scanner(System.in);

        while (!tak.isGameOver()) {
            if (tak.getCurrentPlayerIndex() == 1) {
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
                }
            }

            else {
                try {
                    engine.solve(tak, 0, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple<Move, Double> best = engine.pollMoves().get(0);
                System.out.println(best.one + ": " + best.two);
                tak.executeMove(best.one);
            }

            if (tak.getCurrentPlayerIndex() == 0) {
                System.out.println();
            }
        }

        System.out.println("Game over! " + tak.getGameResult().getMessage());
        System.out.println(tak.getRoadGraphString());
    }
}
