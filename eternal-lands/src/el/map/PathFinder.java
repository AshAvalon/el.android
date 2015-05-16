package el.map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PathFinder {
    private static final int MAX_HEAP_SIZE = 200000;

    public static class Point {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return 1000 * y + x;
        }
    }

    private ElMap m;

    private int width;
    private int height;

    private PointState[][] state;

    private Point[] heap = new Point[MAX_HEAP_SIZE + 1];
    private int heapSize;

    public void setMap(ElMap m) {
        this.m = m;

        this.height = m.height;
        this.width = m.width;

        state = new PointState[height][width];
    }

    public LinkedList<Point> search(Point start, Point end) {
        if(m.heightMap[end.y][end.x] == 0 || start.equals(end)) {
            return new LinkedList<Point>();
        }

        computeParents(start, end);
        LinkedList<Point> path = listParents(end);
        clearState();

        return path;
    }

    private void computeParents(Point start, Point end) {
        state[start.y][start.x] = new PointState();
        state[start.y][start.x].currentCost = 0;
        state[start.y][start.x].estimateLeftCost = estimateDistance(start, end);
        state[start.y][start.x].totalCost = state[start.y][start.x].estimateLeftCost;

        update(start);

        while (heapSize > 0 && heapSize < MAX_HEAP_SIZE) {
            Point next = min();
            state[next.y][next.x].visited = true;

            if(next.equals(end)) {
                break;
            }

            updateNeighbours(next, end);
        }
    }

    private void updateNeighbours(Point current, Point end) {
        for (Point n : neighbours(current)) {
            if(!walkable(current, n)) {
                continue;
            }
            if(state[n.y][n.x] == null) {
                state[n.y][n.x] = new PointState();
                state[n.y][n.x].estimateLeftCost = estimateDistance(n, end);
            }

            int cost = state(current).currentCost + realDistance(current, n);
            if(!state[n.y][n.x].visited && cost < state[n.y][n.x].currentCost) {
                state[n.y][n.x].currentCost = cost;
                state[n.y][n.x].parent = current;
                state[n.y][n.x].totalCost = cost + state[n.y][n.x].estimateLeftCost;
                update(n);
            }
        }
    }

    private int realDistance(Point p1, Point p2) {
        boolean diagonal = (p1.x != p2.x && p1.y != p2.y);
        return diagonal ? 14 : 10;
    }

    private int estimateDistance(Point p1, Point p2) {
        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;

        return (Math.abs(dx) + Math.abs(dy)) << 4;
    }

    private void clearState() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                state[i][j] = null;
            }
        }

        for(int i = 0; i < heapSize; ++i) {
            heap[i] = null;
        }

        heapSize = 0;
    }

    public List<Point> neighbours(Point p) {
        List<Point> neighbours = new ArrayList<Point>(8);

        for (int dx = -1; dx <= 1; dx++) {
            int x = p.x + dx;
            if(x >= 0 && x < width) {
                for (int dy = -1; dy <= 1; dy++) {
                    int y = p.y + dy;
                    if(y >= 0 && y < height && (dx != 0 || dy != 0)) {
                        neighbours.add(new Point(x, y));
                    }
                }
            }
        }

        return neighbours;
    }

    private boolean walkable(Point current, Point next) {
        return Math.abs(m.heightMap[next.y][next.x] - m.heightMap[current.y][current.x]) <= 2;
    }

    private LinkedList<Point> listParents(Point point) {
        LinkedList<Point> result = new LinkedList<Point>();

        if(state(point) == null || !state(point).visited) {
            return result;
        }

        while (point != null) {
            result.addFirst(point);
            point = state(point).parent;
        }

        return result;
    }

    private void update(Point p) {
        if(state(p).heapIndex == -1 && heapSize < MAX_HEAP_SIZE) {
            heap[heapSize] = p;
            state(p).heapIndex = heapSize;
            ++heapSize;
        }

        int i = state(p).heapIndex;

        while(i > 0 && state((i - 1) / 2).totalCost > state(i).totalCost) {
            swapHeap(i, (i - 1) / 2);
            i = (i - 1) / 2;
        }
    }

    private PointState state(int heapIndex) {
        return state[heap[heapIndex].y][heap[heapIndex].x];
    }

    private PointState state(Point p) {
        return state[p.y][p.x];
    }

    private Point min() {
        Point result = heap[0];
        --heapSize;

        if(heapSize == 0) {
            return result;
        }

        heap[0] = heap[heapSize];
        state(0).heapIndex = 0;

        int i = 0;
        int next = getMinHeap(i, i * 2 + 1, i * 2 + 2);

        while(next != i) {
            swapHeap(i, next);
            i = next;
            next = getMinHeap(i, i * 2 + 1, i * 2 + 2);
        }

        return result;
    }

    private int getMinHeap(int i1, int i2, int i3) {
        int min = i1;
        if(i2 < heapSize && state(i2).totalCost < state(min).totalCost) {
            min = i2;
        }
        if(i3 < heapSize &&  state(i3).totalCost < state(min).totalCost) {
            min = i3;
        }

        return min;
    }

    private void swapHeap(int i1, int i2) {
        Point t = heap[i1];
        heap[i1] = heap[i2];
        heap[i2] = t;

        state(i1).heapIndex = i2;
        state(i2).heapIndex = i1;
    }

    private static class PointState {
        private boolean visited = false;

        private int currentCost = Integer.MAX_VALUE;
        private int estimateLeftCost = Integer.MAX_VALUE;
        private int totalCost = Integer.MAX_VALUE;

        private int heapIndex = -1;

        private Point parent;
    }
}
