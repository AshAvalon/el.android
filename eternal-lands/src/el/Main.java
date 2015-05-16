package el;

import el.client.Client;
import el.server.ServerConnection;

import java.io.IOException;

public class Main {
    private static final String HOST = "game.eternal-lands.com";
    private static final int PORT = 2001;

    public static final String USERNAME = "";
    public static final String PASSWORD = "";

    public static void main(String[] args) throws IOException {
        try {
            setUp();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void setUp() throws IOException {
        ServerConnection connection = new ServerConnection(HOST, PORT);
        connection.connect();
        Client client = new Client(connection);
        client.authenticate(USERNAME, PASSWORD);
    }

}
