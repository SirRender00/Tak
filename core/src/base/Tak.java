package base;

import base.move.Move;
import base.move.MoveFactory;
import base.move.PlaceMove;
import base.move.StackMove;

/**
 * The main class that represents a full Tak game. Instantiate this
 * class with a {@link GameType} which is designated by board size
 * (default is five by five). The game progresses by passing in a {@link Move}
 * to {@code Tak.executeSafeMove(...)} (or {@code Tak.executeMove(...)} to
 * turn off validity checks). Once the game has reached an end condition,
 * (as specified by the rules http://cheapass.com/wp-content/uploads/2016/07/Tak-Beta-Rules.pdf),
 * {@code getResult()} will return the corresponding {@link GameResult}. <br> <br>
 *
 * The class represents the Tak board as a 2D array of {@link Stack} objects and keeps track
 * of the players with a 2-element array of {@link Player} objects. An invariant that must
 * be kept among different classes is that the index of the player is the player number.
 * (White is player 0, Black is player 1.)
 */
public class Tak {

    private GameType gameType;
    private Player[] players;
    private Stack[][] board;
    private GameResult result = GameResult.ONGOING;
    private int currentPlayer = 0;
    private boolean firstMove = true;

    private RoadGraph roadGraph;

    /**
     * @param type The GameType that specifies the board size
     *             and player stone amounts.
     */
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

    /**
     * Creates a deep copy of a Tak object.
     * @param other The Tak object to copy.
     */
    public Tak(Tak other) {
        gameType = other.gameType;

        board = new Stack[gameType.size][gameType.size];
        for (int i = 0; i < gameType.size; i++) {
            for (int j = 0; j < gameType.size; j++) {
                board[i][j] = new Stack(other.board[i][j]);
            }
        }

        players = new Player[]{new Player(other.players[0]), new Player(other.players[1])};
        currentPlayer = other.currentPlayer;
        firstMove = other.firstMove;

        roadGraph = new RoadGraph(other.roadGraph);

        result = other.result;
    }

    /**
     * @param x The x coord
     * @param y The y coord
     * @return The stack at the point
     *
     * @throws IndexOutOfBoundsException If x or y is out of bounds
     */
    public Stack getStackAt(int x, int y) {
        if (!inBounds(x) || !inBounds(y)) {
            throw new IndexOutOfBoundsException(x + ", " + y + " is out of bounds.");
        }

        return board[x][y];
    }

    /**
     * @return The size of the board
     */
    public int size() {
        return gameType.size;
    }

    /**
     * @return The player index whose turn it is.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    /**
     * @return The player whose turn it is.
     */
    public Player getCurrentPlayer() {
        return players[getCurrentPlayerIndex()];
    }

    /**
     * @return The player index who "owns" the stone being played.
     * (Read on the rules of Tak about the first moves the
     * of game being played differently.)
     */
    public int getStonePlayer() {
        if (firstMove) {
            return 1 - currentPlayer;
        } else {
            return currentPlayer;
        }
    }

    /**
     * @return The current {@link RoadGraph} representation
     * of the game.
     */
    public RoadGraph getRoadGraph() {
        return roadGraph;
    }

    /**
     * @return {@code true} if and only if the game has ended
     * (Either white wins, black wins, or a tie), {@code false}
     * if the game is ongoing.
     */
    public boolean isGameOver() {
        return !result.equals(GameResult.ONGOING);
    }

    /**
     * @return The {@code GameResult} corresponding to white winning,
     * black winning, tie game, or the game is still ongoing.
     */
    public GameResult getGameResult() {
        return result;
    }

    /**
     * Checks first if the move is valid before trying to execute the move.
     * Recommended if there is any uncertainty that the move may be invalid.
     * @param move The move to execute
     * @throws TakException If the given move is invalid.
     */
    public void safeExecuteMove(Move move) throws TakException {
        StringBuilder message = new StringBuilder();

        if (!validateMove(move, message)) {
            throw new TakException(message.toString());
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
     * @return 0 if player 0 won, 1 if player 1 won, 2 if its a tie, or -1 if the game continues.
     */
    private int checkWin() {
        // if either player won by road, we start with curr player in case of simultaneous win
        int curr = currentPlayer;
        for (int i = 0; i < 2; i++) {
            if (isRoadWin(curr)) {
                return curr;
            }
            curr = 1 - currentPlayer;
        }

        // if the current player does not have any stones remaining or if the board is filled
        MoveFactory.PlaceMoveIterator pIter = new MoveFactory.PlaceMoveIterator(this);
        if (!pIter.hasNext()) {
            return flatStoneWin();
        }

        return -1;
    }

    /**
     * @param player The player to check the road win for
     * @return {@code true} if and only if {@code player} has won by a road
     * connection, {@code false} otherwise.
     */
    private boolean isRoadWin(int player) {
        return isRoadWinG(player);
    }

    /**
     * Implementation of road win by means of a graph, and DFS.
     *
     * @param player The player to check the road win for
     * @return {@code true} if and only if {@code player} has won by a road
     * connection, {@code false} otherwise.
     */
    private boolean isRoadWinG(int player) {
        return roadGraph.isTopToBottom(player) || roadGraph.isLeftToRight(player);
    }

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
        return validateMove(move, new StringBuilder());
    }

    /**
     * @param move The move to validate
     * @param message An empty message container that will contain specific
     *                error messages if this method returns false.
     * @return False if the move is invalid for any reason
     * (not the players turn to play, invalid move), true otherwise
     */
    private boolean validateMove(Move move, StringBuilder message) {
        if (!inBounds(move.x, move.y)) {
            message.append("Move location is out of bounds.");
            return false;
        }

        //first move should place a flat stone
        if (firstMove) {
            if (!(move instanceof PlaceMove)
                    || !((PlaceMove) move).type.equals(Stone.Type.FLAT)) {
                message.append("First moves should place a flat stone.");
                return false;
            }
        }

        if (move instanceof StackMove) {
            return validateStackMove((StackMove) move, message);
        } else {
            return validatePlaceMove((PlaceMove) move, message);
        }
    }

    /**
     * @param n The variable to check
     * @return {@code true} if and only if {@code 0 <= n < board size}.
     */
    public boolean inBounds(int n) {
        return n >= 0 && n < gameType.size;
    }

    /**
     * @param x The x coord
     * @param y the y coord
     * @return {@code true} if and only if {@code inBounds(x) && inBounds(y)}
     */
    public boolean inBounds(int x, int y) {
        return inBounds(x) && inBounds(y);
    }

    /**
     * @param move The {@code PlaceMove} to validate.
     * @param message An empty message container that will contain specific
     *                error messages if this method returns false.
     * @return {@code true} if and only if the move will not
     * cause an error in the course of calling {@code Tak.executeMove(...)},
     * {@code false} otherwise.
     */
    private boolean validatePlaceMove(PlaceMove move, StringBuilder message) {
        message.append("Cannot place stone, ");

        //the tile which a piece is going on better be empty
        if (!getStackAt(move.x, move.y).isEmpty()) {
            message.append("square already occupied.");
            return false;
        }

        //they also should have sufficient pieces
        if (move.type.equals(Stone.Type.CAP)) {
            if (players[getStonePlayer()].capStones <= 0) {
                message.append("out of capstones!");
                return false;
            }
        } else {
            if (players[getStonePlayer()].sideStones <= 0) {
                message.append("out of sidestones!");
                return false;
            }
        }

        return true;
    }

    /**
     * @param move The {@code StackMove} to validate.
     * @param message An empty message container that will contain specific
     *                error messages if this method returns false.
     * @return {@code true} if and only if the move will not
     * cause an error in the course of calling {@code Tak.executeMove(...)},
     * {@code false} otherwise.
     */
    private boolean validateStackMove(StackMove move, StringBuilder message) {
        message.append("Cannot execute stack move, ");

        // current player must own the stack they are trying to move
        if (currentPlayer != getStackAt(move.x, move.y).peek().player) {
            message.append("player does not control stack.");
            return false;
        }

        // pickup amount must be less than or equal to the amount of stones at the point
        if (move.pickup > getStackAt(move.x, move.y).size()) {
            message.append("not enough stones to pickup.");
            return false;
        }

        int tempX = move.x;
        int tempY = move.y;
        int remainingStones = move.pickup;

        for (int i = 0; i < move.vals.length - 1; i++) {
            // should have enough stones to drop down
            if (remainingStones <= 0) {
                message.append("not enough stones to drop down.");
                return false;
            }

            tempX += move.dir.dx;
            tempY += move.dir.dy;

            // everything except the last move should have a flat stone on top
            if (!getStackAt(tempX, tempY).peek().type.equals(Stone.Type.FLAT)) {
                message.append("stone in the path is not flat!");
                return false;
            }

            remainingStones -= move.vals[i];
        }

        tempX += move.dir.dx;
        tempY += move.dir.dy;

        // if the last stone is not flat, it better be a standing stone, and there
        // should only be one remaining piece to drop which should be a cap stone
        if (!getStackAt(tempX, tempY).isEmpty() && !getStackAt(tempX, tempY).peek().type.equals(Stone.Type.FLAT)) {

            if (!(getStackAt(tempX, tempY).peek().type.equals(Stone.Type.STANDING)
                    && getStackAt(move.x, move.y).peek().type.equals(Stone.Type.CAP)
                    && move.vals[move.vals.length - 1] == 1)) {

                message.append("stone in the path is not flat!");
                return false;
            }
        }

        return true;
    }

    /**
     * @return The String representation of the road map.
     * ("W" if white controls it, "B" if black controls it,
     * "_" if no one controls it.)
     */
    public String getRoadGraphString() {
        return roadGraph.toString();
    }

    /**
     * Updates the road graph representation at the point.
     * Sets ownership to the player if the stone is flat,
     * otherwise sets the ownership to -1.
     *
     * @param x The x coord of the stack
     * @param y The y coord of the stack
     */
    public void updateRoadGraph(int x, int y) {
        if (getStackAt(x, y).peek().type.equals(Stone.Type.FLAT)) {
            roadGraph.updateVertex(x, y, getStackAt(x, y).peek().player);
        } else {
            roadGraph.updateVertex(x, y, -1);
        }
    }

    /**
     * Keeps track of the player's "side stone" and cap stone amounts.
     * (Side stones are stones that can be flat or standing.)
     */
    public static class Player {
        private int sideStones;
        private int capStones;

        /**
         * @param sideStones Side stone amount
         * @param capStones Cap stone amount
         */
        Player(int sideStones, int capStones) {
            this.sideStones = sideStones;
            this.capStones = capStones;
        }

        /**
         * Deep copy of player
         *
         * @param player The player to copy
         */
        Player(Player player) {
            sideStones = player.sideStones;
            capStones = player.capStones;
        }

        /**
         * @param type The type of stone to query
         * @return The amount of stones of the specified type
         * (FLAT and STANDING stones are the same type of stone for
         * this purpose)
         */
        public int getRemainingStones(Stone.Type type) {
            if (type.equals(Stone.Type.FLAT) || type.equals(Stone.Type.STANDING)) {
                return sideStones;
            } else if (type.equals(Stone.Type.CAP)) {
                return capStones;
            } else {
                return -1;
            }
        }
    }

    /**
     * A {@code GameType} instantiates a Tak game
     * by board size, starting side stone amount, and
     * starting cap stone amounts.
     */
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

    /**
     * A {@code GameResult} represents a white win, a black win,
     * an ongoing game, or a tie game. It also includes a message,
     * and a payoff which is important for notation or training bots.
     */
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

        /**
         * @return The message associated with this game result.
         */
        public String getMessage() {
            return message;
        }

        /**
         * @return The payoff for white. <br>
         * White wins -> 1 <br>
         * Black wins -> 0 <br>
         * Tie/Ongoing -> 1/2
         */
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
