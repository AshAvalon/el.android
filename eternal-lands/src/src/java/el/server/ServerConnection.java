package el.server;

import el.logging.Logger;
import el.logging.LoggerFactory;
import el.protocol.Message;

import java.io.IOException;
import java.net.Socket;

import static el.utils.IOUtils.closeQuite;

public class ServerConnection extends MessageNotifier {
    private static final Logger LOGGER = LoggerFactory.logger(ServerConnection.class);

    public static final byte[] buffer = new byte[500];
    private final String host;
    private final int port;

    private Socket socket;
    private volatile boolean running;

    public ServerConnection(String host, int port){
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            LOGGER.info("connecting to " + host + ":" + port);
            socket =  new Socket(this.host, this.port);
            return true;
        } catch (IOException e) {
//            LOGGER.error(e);
            closeQuite(socket);
            return false;
        }
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if(running) {
            return;
        }
        running = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                waitMessages();
            }
        });
        thread.start();
    }

    public void stop () {
        LOGGER.info("closing the connection");
        running = false;
        closeQuite(socket);
    }

    public void sendMessage(Message message)  {
        if(!isConnected()) {
            return;
        }

        try {
            notifySend(message);
            optimisticSendMessage(message);
        } catch (IOException e) {
            LOGGER.error("ERROR while sending to server", e);
            stop();
        }
    }

    //sync, so i make sure i don't start a new thread before ending this
    public synchronized void waitMessages() {
        LOGGER.info("accepting messages from server");
        while (isConnected()) {
            try{
                notifyReceive(optimisticReadMessage());
            } catch (IOException e) {
                if(isRunning()) {
//                    LOGGER.error("ERROR while reading from server:", e);
                    stop();
                }
            }
        }
        LOGGER.info("server thread ended");
    }

    private void optimisticSendMessage(Message message) throws IOException {
        if(message.getSource().length > 1) {
            socket.getOutputStream().write(message.getSource(), 0, message.getLength() + 2);
        } else {
            socket.getOutputStream().write(message.getSource()[0]);
        }
    }

    private Message optimisticReadMessage() throws IOException {
        buffer[0] = (byte) socket.getInputStream().read();
        if(buffer[0] == -1) {
            throw new IOException();
        }

        buffer[1] = (byte) socket.getInputStream().read();
        buffer[2] = (byte) socket.getInputStream().read();

        int length = (buffer[2] << 8) | (buffer[1] & 0xFF);
        int remaining =  length - 1;

        while (remaining > 0) {
            int read = socket.getInputStream().read(buffer, length - remaining + 2, remaining);
            remaining -= read;
        }

        return new Message(copyOfRange(buffer, 0, length + 2));
    }

    private byte[] copyOfRange(byte[] original, int from, int to){
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength));
        return copy;
    }
}
