package base;

import base.move.Move;
import base.move.PlaceMove;
import base.move.StackMove;

public class Tak {

    private GameType gameType;
    private Player[] players;
    private Stack[][] board;
    private GameResult result = GameResult.ONGOING;
    public int currentPlayer = 0;
    private boolean firstMove = true;

    private RoadGraph roadGraph;

    public Tak(GameType type) {
        this.gameType = type;

        board = new Stack[gameType.size][gameType.size];
        for (int i = 0; i < gameType.size; i++) {
            for (int j = 0; j < gameType.size; j++) {
                board[i][j] = new Stack(gameType.size);
            }
        }

        players = new Player[]{new Player(gameType.sideStones, gameType.capStones),
                                new Player(gameType.sideStones, gameType.capStones)};

        roadGraph = new RoadGraph(gameType.size);
    }

    public Tak(Tak tak) {

    }

    public Stack getStackAt(int x, int y) {
        return board[x][y];
    }

    public int getCurrentPlayer() {
        if (firstMove) {
            return 1 - currentPlayer;
        } else {
            return currentPlayer;
        }
    }

    public RoadGraph getRoadGraph() {
        return roadGraph;
    }

    public boolean isGameOver() {
        return !result.equals(GameResult.ONGOING);
    }

    public GameResult getGameResult() {
        return result;
    }

    /**
     * @param move The move to execute
     * @throws TakException If the given move is invalid
     */
    public void safeExecuteMove(Move move) throws TakException {
        if (!validateMove(move)) {
            throw new TakException("Invalid Move");
        }

        if(isGameOver()) {
            throw new TakException("Game is over");
        }

        executeMove(move);
    }

    /**
     * Executes the given move without checking for validity.
     * Use <code>safeExecuteMove(...)</code> for checks.
     * @param move The move to execute
     */
    public void executeMove(Move move) {
        if (firstMove && currentPlayer == 1) {
            move.action(this);
            firstMove = false;
        } else {
            move.action(this);
        }

        switch (checkWin()) {
            case -1: currentPlayer = 1 - currentPlayer; break;
            case 0: result = GameResult.WHITE; break;
            case 1: result = GameResult.BLACK; break;
            case 2: result = GameResult.TIE; break;
        }
    }

    /**
     * @return 0 if player 0 won, 1 if player 1 won, 2 if its a tie, or -1 if the game continues
     */
    private int checkWin() {
        for (int i = currentPlayer; i != 1 - currentPlayer; i = 1 - currentPlayer) {
            if (isRoadWin(i)) {
                return i;
            }
        }

        for (int i = currentPlayer; i != 1 - currentPlayer; i = 1 - currentPlayer) {
            if (players[i].sideStones == 0 && players[i].capStones == 0) {
                return flatStoneWin();
            }
        }

        return -1;
    }

    private boolean isRoadWin(int player) {
        return isRoadWinG(player);
    }

    //BLOCK 0: NAIVE isRoadWin SOLUTION

    private boolean isRoadWinG(int player) {
        return roadGraph.isTopToBottom(player) || roadGraph.isLeftToRight(player);
    }

    //END BLOCK 0

    /**
     * @return 0 if player 0 has more flat stones on top,
     * 1 if player 1 has more flat stones on top,
     * 2 if they have the same amount
     */
    private int flatStoneWin() {
        int result = 0;

        for (int i = 0; i < gameType.size; i++) {
            for (int j = 0; j < gameType.size; j++) {
                Stone s = getStackAt(i, j).peek();
                if (s.type.equals(Stone.Type.FLAT)) {
                    if (s.player == 0) {
                        result -= 1;
                    } else {
                        result += 1;
                    }
                }
            }
        }

        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return 0;
        } else {
            return 2;
        }
    }

    /**
     * @param move The move to validate
     * @return False if the move is invalid for any reason
     * (not the players turn to play, invalid move), true otherwise
     */
    public boolean validateMove(Move move) {
        if (!(inBounds(move.x) && inBounds(move.y))) {
            return false;
        }

        //first move should place a flat stone
        if (firstMove) {
            if (!(move instanceof PlaceMove)
                    || !((PlaceMove) move).type.equals(Stone.Type.FLAT)) {
                return false;
            }
        }

        if (move instanceof StackMove) {
            return validateStackMove((StackMove) move);
        } else {
            return validatePlaceMove((PlaceMove) move);
        }
    }

    private boolean inBounds(int n) {
        return n >= 0 && n < gameType.size;
    }

    private boolean validatePlaceMove(PlaceMove move) {
        //the tile which a piece is going on better be empty
        if (!getStackAt(move.x, move.y).isEmpty()) {
            return false;
        }

        //they also should have sufficient pieces
        if (move.type.equals(Stone.Type.CAP)) {
            return players[getCurrentPlayer()].capStones > 0;
        } else {
            return players[getCurrentPlayer()].sideStones > 0;
        }
    }

    private boolean validateStackMove(StackMove move) {
        //pickup amount must be less than or equal to the amount of stones at the point
        if (getStackAt(move.x, move.y).size() > move.pickup) {
            return false;
        }

        int tempX = move.x;
        int tempY = move.y;
        int remainingStones = move.pickup;
        for (int i = 0; i < move.vals.length - 1; i++) {
            remainingStones -= move.vals[i];
            tempX += move.dir.dx;
            tempY += move.dir.dy;

            //should have enough stones to drop down
            if (remainingStones < 0) {
                return false;
            }

            //everything except the last move should have a flat stone on top
            if (!getStackAt(tempX, tempY).peek().type.equals(Stone.Type.FLAT)) {
                return false;
            }
        }

        tempX += move.dir.dx;
        tempY += move.dir.dy;

        //if the last stone is not flat, it better be a standing stone, and the remaining piece
        //better be a cap stone
        if (!getStackAt(tempX, tempY).isEmpty() && !getStackAt(tempX, tempY).peek().type.equals(Stone.Type.FLAT)) {
            return getStackAt(tempX, tempY).peek().type.equals(Stone.Type.STANDING)
                    && getStackAt(move.x, move.y).peek().type.equals(Stone.Type.CAP)
                    && move.vals[move.vals.length - 1] == 1;
        }

        return true;
    }

    public void printRoaGraph() {
        roadGraph.print();
    }

    private static class Player {
        int sideStones;
        int capStones;

        public Player(int sideStones, int capStones) {
            this.sideStones = sideStones;
            this.capStones = capStones;
        }
    }

    public enum GameType {

        FIVE(21, 1, 5);

        int sideStones;
        int capStones;
        int size;

        GameType(int sideStones, int capStones, int size) {
            this.sideStones = sideStones;
            this.capStones = capStones;
            this.size = size;
        }
    }

    public enum GameResult {

        WHITE("White Wins!", 1),
        BLACK("Black Wins!", 0),
        ONGOING("The game is ongoing.", 1 / 2),
        TIE("Game was a tie!", 1 / 2);

        String message;
        double whiteWin;

        GameResult(String str, double d) {
            message = str;
            whiteWin = d;
        }

        public String getMessage() {
            return message;
        }

        public double getWhiteWin() {
            return whiteWin;
        }
    }

    public static class TakException extends Exception {
        TakException(String msg) {
            super(msg);
        }
    }
}
