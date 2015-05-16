package el.protocol;

import android.util.Log;

import el.actor.Item;

import java.io.UnsupportedEncodingException;

import static el.protocol.ProtocolTypes.*;

public class Messages {
    private static final Message HEART_BEAT_MESSAGE = new Message(new byte[]{HEART_BEAT, 1, 0});

    //todo: change to a string
    public static Message version(byte[] version) {
        return new Message(version);
    }

    public static Message authentication(String username, String password) {
        try {
            byte[] data = (username + " " + password).getBytes("ISO8859-1");
            return new Message(bytes(LOG_IN, (short) (data.length + 2), data));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message heartBeat() {
        return HEART_BEAT_MESSAGE;
    }

    public static Message rawText(String text) {
        byte type = RAW_TEXT;
        if(text.charAt(0) == '/') {
            type = SEND_PM;
            text = text.substring(1); //skipping the "/" character
        } else if(text.charAt(0) == '#') {
            text = text + " "; //you know you are doing it wrong when you write comments like these, but here it goes: i don't know why it works this way
        }

        try {
            byte[] data = text.getBytes("ISO8859-1");
            return new Message(bytes(type, (short)(data.length + 2), data));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message useItem(int invPos) {
        return new Message(bytes(USE_INVENTORY_ITEM, (short)2, new byte[]{(byte) invPos}));
    }

    public static Message sendOpeningScreen() {
        return new Message(new byte[]{SEND_OPENING_SCREEN});
    }

    public static Message moveTo(int x, int y) {
        return new Message(bytes(MOVE_TO, (short) 5, new byte[]{(byte) (x & 0xFF), (byte) ((x >> 8) & 0xFF), (byte) (y & 0xFF), (byte) ((y >> 8) & 0xFF)}));
    }

    public static Message harvest(int itemId) {
        return new Message(bytes(HARVEST, (short)3, new byte[]{(byte) (itemId & 0xFF), (byte) ((itemId >> 8) & 0xFF)}));
    }

    public static Message useMapObject(int objectId, int withId) {
        return new Message(bytes(USE_MAP_OBJECT, (short) 9, new byte[]{
                (byte) (objectId & 0xFF), (byte) (objectId >> 8 & 0xFF), (byte) (objectId >> 16 & 0xFF), (byte) (objectId >> 24 & 0xFF),
                (byte) (withId & 0xFF), (byte) (withId >> 8 & 0xFF), (byte) (withId >> 16 & 0xFF), (byte) (withId >> 24 & 0xFF)
        }));
    }

    public static Message touchActor(int actorId) {
        return new Message(new byte[]{
                TOUCH_PLAYER,
                5, 0,
                (byte) (actorId & 0xFF), (byte) (actorId >> 8 & 0xFF), (byte) (actorId >> 16 & 0xFF), (byte) (actorId >> 24 & 0xFF)
        });
    }

    public static Message moveInventoryItem(int fromPos, int toPos) {
        return new Message(new byte[]{MOVE_INVENTORY_ITEM, 3, 0, (byte) (fromPos), (byte) (toPos)});
    }

    public static Message respondToNpc(int npcId, int optionId) {
        return new Message(new byte[] {RESPOND_TO_NPC,
                5, 0,
                (byte) (npcId & 0xFF), (byte) ((npcId >> 8) & 0xFF),
                (byte) (optionId & 0xFF), (byte) ((optionId >> 8) & 0xFF),
        });
    }

    public static Message getStorageCategory(int category) {
        return new Message(new byte[]{ GET_STORAGE_CATEGORY,
                2, 0,
                (byte) category
        });
    }

    public static Message deposit(int itemPos, int quantity) {
        return new Message(new byte[]{ DEPOSITE_ITEM,
                6, 0,
                (byte)(itemPos & 0xFF),
                (byte) (quantity & 0xFF), (byte) (quantity >> 8 & 0xFF), (byte) (quantity >> 16 & 0xFF), (byte) (quantity >> 24 & 0xFF)
        });
    }

    public static Message withdraw(int itemPos, int quantity) {
        return new Message(new byte[]{ WITHDRAW_ITEM,
                7, 0,
                (byte) (itemPos & 0xFF), (byte) ((itemPos >> 8) & 0xFF),
                (byte) (quantity & 0xFF), (byte) (quantity >> 8 & 0xFF), (byte) (quantity >> 16 & 0xFF), (byte) (quantity >> 24 & 0xFF)
        });
    }

    public static Message tradeWith(int actorId) {
        return new Message(new byte[]{ TRADE_WITH,
                5, 0,
                (byte) (actorId & 0xFF), (byte) (actorId >> 8 & 0xFF), (byte) (actorId >> 16 & 0xFF), (byte) (actorId >> 24 & 0xFF)
        });
    }

    public static Message putObjectOnTrade(int itemPos, int quantity, boolean fromStorage) {
        return new Message(new byte[]{PUT_OBJECT_ON_TRADE,
                7, 0,
                (byte) (fromStorage ? 2 : 1),
                (byte) (itemPos & 0xFF),
                (byte) (quantity & 0xFF), (byte) (quantity >> 8 & 0xFF), (byte) (quantity >> 16 & 0xFF), (byte) (quantity >> 24 & 0xFF)
        });
    }

    public static Message removeObjectFromTrade(int itemPos, int quantity) {
        return new Message(new byte[]{ REMOVE_OBJECT_FROM_TRADE,
                6, 0,
                (byte) (itemPos & 0xFF),
                (byte) (quantity & 0xFF), (byte) (quantity >> 8 & 0xFF), (byte) (quantity >> 16 & 0xFF), (byte) (quantity >> 24 & 0xFF)
        });
    }

    public static Message exitTrade() {
        return new Message(new byte[] {EXIT_TRADE, 1, 0});
    }

    public static Message acceptTrade(int myAccept, Item[] hisItems) {
        byte data[] = new byte[1 + 2 + 16];
        data[0] = ACCEPT_TRADE;
        data[1] = 17;
        data[2] = 0;
        if(myAccept == 1) {
            for (int i = 0; i < hisItems.length; i++) {
                if (hisItems[i] != null && hisItems[i].quantity > 0) {
                    data[i + 3] = (byte) (hisItems[i].type & 0xFF);
                }
            }
        }
        return new Message(data);
    }

    public static Message rejectTrade() {
        return new Message(new byte[]{REJECT_TRADE, 1, 0});
    }

    private static byte[] bytes(int type, short length, byte[] data) {
        byte[] bytes = new byte[length + 2];

        bytes[0] = (byte) (type & 0xFF);
        bytes[1] = (byte) (length & 0xFF);
        bytes[2] = (byte)((length >> 8) & 0xFF);

        System.arraycopy(data, 0, bytes, 3, data.length);

        return bytes;
    }
    public static Message manufacture(Item[] manufacture_items, int mix_num){
        byte type = MANUFACTURE_THIS;
        int num_items = 0;

        // Get total number of items in pipe
        for(int i = 0; i < manufacture_items.length; i++) {
            if(manufacture_items[i] != null) {
                num_items++;
            }
        }

        // Message contains in order:
        // Data type (MANUFACTURE_THIS, 1 byte)
        // Message length (2 bytes)
        // Number of items in recipe (1 byte)
        // For each item:
        // Item pos (1 byte)
        // Item quantity (2 bytes)
        // Mix quantity (1 byte at the end of message)

        // Total bytes in a message then:
        // number_of_items * 3 + 5

        byte data[] = new byte[num_items * 3 + 5];
        // Message type
        data[0] = type;
        //Message length
        data[1] = (byte) ((num_items * 3 + 3) & 0xFF);
        data[2] = (byte) ((num_items * 3 + 3) >> 8  & 0xFF);
        //Number of items in recipe
        data[3] = (byte) (num_items & 0xFF);

        for(int i = 0; i < num_items; i++) {
            // Item position corresponding to inventory position
            data[i * 3 + 4] = (byte) (manufacture_items[i].pos);
            // quantity could be bigger than 255 (in recipes), and the server
            // expects the quantity in i*3+5th and i*3+6th bytes.
            data[i * 3 + 5] = (byte) ( (manufacture_items[i].quantity ) & 0xFF);
            data[i * 3 + 6] = (byte) ( (manufacture_items[i].quantity >>8) & 0xFF);
        }
        // Last byte in message is the mix quantity (1 or 255)
        data[num_items * 3 + 4] = (byte) (mix_num & 0xFF);
        return new Message(data);
    }

    // Be polite to people and sit down while harvesting (simple message)
    // Does not toggle as in the desktop client but just sits down.
    public static Message sitDown() {
        return new Message(new byte[]{SIT_DOWN, 2, 0, 1});
    }

}
