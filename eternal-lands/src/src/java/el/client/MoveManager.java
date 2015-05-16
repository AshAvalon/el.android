package el.client;

import el.logging.Logger;
import el.logging.LoggerFactory;
import el.map.ElMap;
import el.map.PathFinder;
import el.protocol.Messages;
import el.server.ServerConnection;

import java.util.LinkedList;

import static el.map.PathFinder.Point;

public class MoveManager {
    private static final Logger LOGGER = LoggerFactory.logger(MoveManager.class);

    private static final int MAX_STEPS = 10;

    private ServerConnection connection;

    private int x;
    private int y;

    private boolean walking;
    private LinkedList<Point> path;

    private PathFinder pathFinder = new PathFinder();

    public MoveManager(ServerConnection connection) {
        this.connection = connection;
    }

    public void setMap(ElMap map) {
        pathFinder.setMap(map);
    }

    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        doNextStep();
    }

    public synchronized boolean moveTo(int toX, int toY) {
        //stop prev walking
        walking = false;

        LinkedList<Point> newPath = pathFinder.search(new Point(x, y), new Point(toX, toY));
        if(newPath.isEmpty()) {
            return false;
        }

        path = minimize(newPath);

        walking = true;
        doNextStep();

        return true;
    }

    public LinkedList<Point> minimize(LinkedList<Point> path) {
        LinkedList<Point> result = new LinkedList<>();

        int index = 0;
        for (Point point : path) {
            if(index % MAX_STEPS == 0) {
                result.addLast(point);
            }
            ++index;
        }

        if(!result.getLast().equals(path.getLast())) {
            result.addLast(path.getLast());
        }

        return result;
    }

    private void doNextStep() {
        if(!walking || path.isEmpty()) {
            return;
        }

        Point next = path.peek();
        if(next.x != x || next.y != y) {//make next move?
            return;//no
        }

        path.poll();
        if(path.isEmpty()) {
            walking = false;//no more steps to make
            return;
        }

        next = path.peek();
        LOGGER.info("move request to " + next.x + " " + next.y);
        connection.sendMessage(Messages.moveTo(next.x, next.y));
    }
}
