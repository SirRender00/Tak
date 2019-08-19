package engine;

public abstract class Model {

    protected abstract void update(GameTree tree, double outcome);

    protected abstract void train(GameTree tree);
}
