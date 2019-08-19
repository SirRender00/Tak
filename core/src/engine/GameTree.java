package engine;

import base.Tak;
import base.move.Move;
import base.move.MoveFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameTree {

    public Tak tak;
    private List<GameTree> children;
    private Iterator<GameTree> childrenGenerator;
    public int timesVisited = 0;
    public int favorsWhite = 0;

    public GameTree(Tak tak) {
        this.tak = tak;
        childrenGenerator = new ChildIterator(MoveFactory.allPossibleMoves(tak));
    }

    public double getPercentFavorsWhite() {
        return ( (double) favorsWhite) / timesVisited;
    }

    public double getPercentFavorsBlack() {
        return 1 - getPercentFavorsWhite();
    }

    public void releaseResources() {
        tak = null;
        childrenGenerator = null;
    }

    public Iterator<GameTree> getChildren() {
        if (children.isEmpty()) {
            children = new ArrayList<>(20);
            return childrenGenerator;
        } else {
            return children.iterator();
        }
    }

    private class ChildIterator implements Iterator<GameTree> {

        Iterator<Move> moveIterator;

        ChildIterator(Iterator<Move> moveIterator) {
            this.moveIterator = moveIterator;
        }

        @Override
        public boolean hasNext() {
            return moveIterator.hasNext();
        }

        @Override
        public GameTree next() {
            Tak nextTak = new Tak(tak);
            nextTak.executeMove(moveIterator.next());
            GameTree tree = new GameTree(nextTak);

            children.add(tree);

            return tree;
        }
    }
}
