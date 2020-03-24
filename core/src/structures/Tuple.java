package structures;

/**
 * A <code>Tuple</code> has two public fields <code>one</code> and <code>two</code>.
 */
public class Tuple<K, V> {

    public K one;
    public V two;

    public Tuple(K one, V two) {
        this.one = one;
        this.two = two;
    }
}
