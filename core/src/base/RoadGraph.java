package base;

import structures.Direction;
import structures.Point;

import java.util.List;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Arrays;

public class RoadGraph {

    private int[] vertices;
    private int size;
    private List<Integer> ALL_LEFT_NODES;
    private List<Integer> ALL_TOP_NODES;

    private VirtualNode TOP;
    private VirtualNode BOTTOM;
    private VirtualNode LEFT;
    private VirtualNode RIGHT;

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

    private int pointToVertex(int x, int y) {
        return x + size * y;
    }

    private Point vertexToPoint(int n) {
        return new Point(n % size, n / size);
    }

    public void updateVertex(int x, int y, int player) {
        vertices[pointToVertex(x, y)] = player;
    }

    public boolean isLeftToRight() {
        return isConnected(LEFT, RIGHT);
    }

    public boolean isTopToBottom() {
        return isConnected(TOP, BOTTOM);
    }

    private boolean isConnected(VirtualNode start, VirtualNode end) {
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

            toVisit.addAll(neighborsOf(node, visited));
        }

        return false;
    }

    public List<Integer> neighborsOf(int n, boolean[] visited) {
        List<Integer> result = new ArrayList<>(4);

        for (Direction dir : Direction.values()) {
            int val = pointToVertex(dir.dx, dir.dy);
            int pNeighbor = n + val;

            if (pNeighbor >= 0 && pNeighbor < size * size
                    && !visited[pNeighbor] && vertices[pNeighbor] == vertices[n]) {
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

    public List<Integer> neighborsOf(VirtualNode n) {
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
