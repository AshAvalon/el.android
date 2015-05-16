package el.protocol;

import static el.utils.ByteUtils.nex2;
import static el.utils.ByteUtils.unsigned;

public class Message {
    private final byte[] source; //immutable

    public Message(byte[] source) {
        this.source = source;
    }

    public int getType() {
        return unsigned(source[0]);
    }

    public int getLength() {
        return nex2(source, 1);
    }

    public byte[] getSource() {
        return source;
    }
}
