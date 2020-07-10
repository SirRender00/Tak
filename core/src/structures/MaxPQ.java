package structures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.List;

import static java.lang.Integer.min;


/**
 * A PriorityQueue that orders elements of type <code>T</code> by maximum.
 * You can instantiate the PQ by giving it a <code>Comparator</code> that
 * compares objects of type <code>T</code>. If no comparator is given, the PQ
 * tries to use the the natural ordering of <code>T</code> objects as specified
 * by <code>Comparator.naturalOrder()</code> in the {@link Comparator} class.
 */
public class MaxPQ<T> {

    private Comparator<T> comp;

    private List<T> objects;

    public MaxPQ() {
        comp = (Comparator<T>) Comparator.naturalOrder();
    }

    public MaxPQ(Comparator<T> comp) {
        this.comp = comp;
        this.objects = new ArrayList<>();
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int leftChild(int index) {
        return 2 * index + 1;
    }

    public void add(T obj) {
        objects.add(obj);
        swim(objects.size() - 1);
    }

    private void sink(int index) {
        int child1 = leftChild(index);
        int child2 = child1 + 1;

        if (child1 >= objects.size()) {
            return;
        }

        int child = child1;

        if (child2 < objects.size()) {
            if (comp.compare(objects.get(child2), objects.get(child1)) < 0) {
                child = child2;
            }
        }

        if (comp.compare(objects.get(index), objects.get(child)) < 0) {
            swap(index, child);
            sink(child);
        }
    }

    private void swim(int index) {
        if (index == 0) {
            return;
        }

        T parent = objects.get(parent(index));
        T obj = objects.get(index);

        if (comp.compare(obj, parent) > 0) {
            swap(parent(index), index);
            swim(parent(index));
        }
    }

    private void swap(int index1, int index2) {
        T temp = objects.get(index1);
        objects.set(index1, objects.get(index2));
        objects.set(index2, temp);
    }

    public T pop() {
        if (objects.isEmpty()) {
            throw new EmptyStackException();
        }

        swap(0, objects.size() - 1);
        T obj = objects.remove(objects.size() - 1);
        sink(0);

        return obj;
    }

    public T peek() {
        if (objects.isEmpty()) {
            throw new EmptyStackException();
        }

        return objects.get(0);
    }

    public List<T> toSortedList(int n) {
        MaxPQ<T> copy = new MaxPQ<>(comp);
        copy.objects = new ArrayList<>(objects);

        List<T> sorted = new ArrayList<>(objects.size());

        int len = min(objects.size(), n);
        for (int i = 0; i < len; i++) {
            sorted.add(copy.pop());
        }

        return sorted;
    }

    public void changeMax(T obj) {
        objects.set(0, obj);
        sink(0);
    }
}
