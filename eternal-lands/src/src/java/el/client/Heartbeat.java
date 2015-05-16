package el.client;


import el.logging.Logger;
import el.logging.LoggerFactory;
import el.protocol.Messages;
import el.server.ServerConnection;

public class Heartbeat extends Thread{
    private static final Logger LOGGER = LoggerFactory.logger(Heartbeat.class);
    private ServerConnection connection;

    private volatile boolean running = true;

    public Heartbeat(ServerConnection connection) {
        this.connection = connection;
        start();
    }

    public void die() {
        running = false;
    }

    @Override
    public void run() {
        while (running && connection.isRunning()) {
            LOGGER.info("Sending heart beat");
            connection.sendMessage(Messages.heartBeat());
            try {
                sleep(25000);
            } catch (InterruptedException e) {
                //we don't care, just go on
            }
        }
        LOGGER.info("server disconnected, ending heart beat thread");
    }
}
