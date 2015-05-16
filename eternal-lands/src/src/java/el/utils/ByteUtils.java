package el.utils;

public class ByteUtils {
    public static int unsigned(byte b) {
        return b & 0xFF;
    }

    public static int nex2(byte[] data, int start) {
        return ((data[start + 1] & 0xFF) << 8) | (data[start] & 0xFF);
    }

    public static int nex4(byte[] data, int start) {
        return
                (data[start + 3] & 0xFF) << 24 |
                (data[start + 2] & 0xFF) << 16 |
                (data[start + 1] & 0xFF) << 8  |
                data[start] & 0xFF;
    }

    public static int find(byte[] bytes, int start, int value) {
        int index = start;
        while(index < bytes.length && bytes[index] != value) {
            index++;
        }
        return index == bytes.length ? -1 : index;
    }

}
