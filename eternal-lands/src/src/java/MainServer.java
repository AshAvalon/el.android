import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(8000);
        Socket socket = server.accept();

        read(socket);//version

        write(socket, 0, 16, "3905465737420536572766572210a");
        write(socket, 60, 5, "8bc52121");

        read(socket);//credentials

        write(socket, 7, 21, "2e2f6d6170732f73746172746d61702e656c6d00");

        read(socket);
        read(socket);
        read(socket);
        read(socket);
    }

    private static void read(Socket accept) throws IOException {
        byte[] data = new byte[255];
        int read = accept.getInputStream().read(data);

        System.out.println("read " + read + " bytes:");
        printBufferHex(data);
    }

    private static void write(Socket socket, int type, int length, String data) throws IOException {
        BigInteger bigInteger = new BigInteger(data, 16);

        byte[] buffer = new byte[length + 2];

        buffer[0] = (byte) type;
        buffer[1] = (byte) (length & 0xff);
        buffer[2] = (byte)((length >> 8) & 0xFF);

        byte[] bytes = bigInteger.toByteArray();

        int start = bytes.length == length - 1 ? 0 : 1;
        System.arraycopy(bytes, start, buffer, 3, bytes.length - start);

        System.out.println("writing:");
        printBufferHex(buffer);

        socket.getOutputStream().write(buffer);
    }

    private static void printBufferHex(byte[] bytes) {
        System.out.println((bytes[0] & 0xff) + " " + new BigInteger(1, bytes).toString(16));
    }
}
