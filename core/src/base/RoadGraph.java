package base;

import structures.Direction;
import structures.Point;

import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Arrays;

/**
 * Used for a naive implementation to check for road wins.
 * We keep track of who owns what square with the vertices array
 * (i.e. vertices[i] = 0 if player 0 owns it, = 1 if player 1 owns it,
 * and = -1 if no one owns it or it is a standing stone). We also create
 * four virtual nodes TOP, BOTTOM, LEFT, RIGHT, whose neighbors are
 * all TOP vertices, all BOTTOM vertices, etc. To prevent "backwash"
 * (say, that TOP and RIGHT are connected via two corners and BOTTOM),
 * TOP and LEFT have directed edges <em>to</em> the board and
 * BOTTOM and RIGHT have directed edges <em>from</em> the board.
 * So, TOP has neighbors ALL_TOP_NODES and LEFT has neighbors
 * ALL_LEFT_NODES. BOTTOM and RIGHT have no neighbors, but
 * any right vertex has RIGHT as a neighbor, and any bottom vertex
 * has BOTTOM as a neighbor. <br> <br>
 * The invariant that vertices[i] = 0 if player 0 owns it, = 1 if player 1 owns it,
 * and = -1 if no one owns it or it is a standing stone must be kept in other classes
 * using this class, otherwise this class will not function as expected.
 */
public class RoadGraph {

    private int[] vertices;
    private int size;
    private List<Integer> ALL_LEFT_NODES;
    private List<Integer> ALL_TOP_NODES;

    private VirtualNode TOP;
    private VirtualNode BOTTOM;
    private VirtualNode LEFT;
    private VirtualNode RIGHT;

    /**
     * Initialize the Road Graph with no one owning anything.
     * @param size The size of the board
     */
    public RoadGraph(int size) {
        this.size = size;
        vertices = new int[size * size + 4];

        Arrays.fill(vertices, -1);

        TOP = new VirtualNode(size * size);
        BOTTOM = new VirtualNode(size * size + 1);
        LEFT = new VirtualNode(size * size + 2);
        RIGHT = new VirtualNode(size * size + 3);

        ALL_LEFT_NODES = new ArrayList<>(size);
        ALL_TOP_NODES = new ArrayList<>(size);
        for (int i = 0; i <= size * size - size; i += size) {
            ALL_LEFT_NODES.add(i);
        }

        for (int i = size * size - size; i < size * size; i += 1) {
            ALL_TOP_NODES.add(i);
        }
    }

    /**
     * Deep copy of <code>graph</code>
     * @param graph The graph to copy
     */
    public RoadGraph(RoadGraph graph) {
        this.size = graph.size;
        vertices = new int[graph.vertices.length];
        System.arraycopy(graph.vertices, 0, vertices, 0, vertices.length);

        TOP = graph.TOP;
        BOTTOM = graph.BOTTOM;
        LEFT = graph.LEFT;
        RIGHT = graph.RIGHT;

        ALL_LEFT_NODES = graph.ALL_LEFT_NODES;
        ALL_TOP_NODES = graph.ALL_TOP_NODES;
    }

    private int pointToVertex(int x, int y) {
        return x + size * y;
    }

    private Point vertexToPoint(int n) {
        return new Point(n % size, n / size);
    }

    /**
     * @param x The x coord of the stack
     * @param y The y coord of the stack
     * @param player The player who owns the stack at x, y
     */
    public void updateVertex(int x, int y, int player) {
        vertices[pointToVertex(x, y)] = player;
    }

    /**
     * @param player The player to check road win from left to right
     * @return True if player has a road from left to right. False otherwise.
     */
    public boolean isLeftToRight(int player) {
        return isConnected(LEFT, RIGHT, player);
    }

    /**
     * @param player The player to check road win from top to bottom
     * @return True if player has a road from top to bottom. False otherwise.
     */
    public boolean isTopToBottom(int player) {
        return isConnected(TOP, BOTTOM, player);
    }

    /**
     * Performs a depth first search from start to end, considering only vertices
     * owned by the player and not already visited.
     *
     * @param start The virtual node to start at (either TOP or LEFT)
     * @param end The virtual node to end at (either BOTTOM or RIGHT)
     * @param player The player whose road may connect start and end
     * @return True if there is a path from start to end, False otherwise.
     */
    private boolean isConnected(VirtualNode start, VirtualNode end, int player) {
        Queue<Integer> toVisit = new ArrayDeque<>(neighborsOf(start));
        boolean[] visited = new boolean[size * size];

        while (!toVisit.isEmpty()) {
            int node = toVisit.remove();
            if (node == end.value) {
                return true;
            }
            else if (node >= size * size){
                continue;
            }
            else {
                visited[node] = true;
            }

            toVisit.addAll(neighborsOf(node, visited, player));
        }

        return false;
    }

    /**
     * Used to print who owns what vertex
     */
    public void print() {
        for (int i = 0; i < size * size; i++) {
            if (i % 5 == 0) {
                System.out.println();
            }

            switch (vertices[i]) {
                case 0: System.out.print("W"); break;
                case 1: System.out.print("B"); break;
                case -1: System.out.print("_"); break;
            }
            System.out.print(" ");
        }
    }

    /**
     * @param n The vertex
     * @param visited boolean array, telling if we have visited a vertex
     * @param player Two vertices are neighbors if they share the same player
     * @return A list of neighbors of n, if we haven't visited them before, and
     * if they belong to the same player
     */
    private List<Integer> neighborsOf(int n, boolean[] visited, int player) {
        List<Integer> result = new ArrayList<>(4);

        for (Direction dir : Direction.values()) {
            int val = pointToVertex(dir.dx, dir.dy);
            int pNeighbor = n + val;

            if (pNeighbor >= 0 && pNeighbor < size * size
                    && !visited[pNeighbor]
                    && vertices[pNeighbor] == vertices[n] && vertices[n] == player) {
                result.add(pNeighbor);
            }
        }

        Point p = vertexToPoint(n);
        if (p.x == size - 1) {
            result.add(RIGHT.value);
        }

        if (p.y == 0) {
            result.add(BOTTOM.value);
        }

        return result;
    }

    /**
     * We also create four virtual nodes TOP, BOTTOM, LEFT, RIGHT, whose
     * neighbors are all TOP vertices, all BOTTOM vertices, etc. To prevent
     * "backwash"(say, that TOP and RIGHT are connected via two corners and
     * BOTTOM), TOP and LEFT have directed edges <em>to</em> the board and
     * BOTTOM and RIGHT have directed edges <em>from</em> the board.
     * So, TOP has neighbors ALL_TOP_NODES and LEFT has neighbors
     * ALL_LEFT_NODES. BOTTOM and RIGHT have no neighbors, but
     * any right vertex has RIGHT as a neighbor, and any bottom vertex
     * has BOTTOM as a neighbor.
     * @param n The virtual node to check for neighbors
     * @return A list of neighbors as specified above, filtering out
     * those that no player owns.
     */
    private List<Integer> neighborsOf(VirtualNode n) {
        ArrayList<Integer> result = new ArrayList<>(size);
        if (n.equals(LEFT)) {
            result = new ArrayList<>(ALL_LEFT_NODES);
        } else if (n.equals(TOP)) {
            result = new ArrayList<>(ALL_TOP_NODES);
        }

        result.removeIf(i -> vertices[i] == -1);

        return result;
    }

    public static class VirtualNode {

        int value;

        VirtualNode(int v) {
            value = v;
        }
    }
}
