package el.client;

import el.actor.Actor;
import el.actor.Item;
import el.protocol.Message;

import static el.utils.ByteUtils.*;

public class InventoryUtil {
    public static boolean ITEM_UID_ENABLED = false;

    public static void putInventory(Actor actor, Message message) {
        clearAllItems(actor);

        int items = unsigned(message.getSource()[3]);
        int itemPackageSize = calculateItemPackageSize(message.getSource(), items);

        ITEM_UID_ENABLED = itemPackageSize == 10;

        for(int i = 0; i < items; ++i) {
            int firstByte = i * itemPackageSize + 4;

            putItem(actor, message, firstByte);
        }
    }

    public static void putItems(Actor actor, Message message) {
        int itemPackageSize = ITEM_UID_ENABLED ? 10 : 8;

        for(int i = 3; i < message.getSource().length; i += itemPackageSize) {
            putItem(actor, message, i);
        }
    }

    public static void getItemCooldown(Actor actor, Message message) {
        for (int i = 3; i < message.getLength(); i += 5) {
            int pos = unsigned(message.getSource()[i]);

            actor.inventory[pos].cooldownMax = 1000 * nex2(message.getSource(), i + 1);
            actor.inventory[pos].cooldownLeft = 1000 * nex2(message.getSource(), i + 3);
        }
    }

    public static void removeItemsFromInventory(Actor actor, Message message) {
        for(int i = 3; i < message.getLength() + 2; ++i) {
            int pos = unsigned(message.getSource()[i]);
            actor.inventory[pos] = null;
        }
    }

    private static void putItem(Actor actor, Message message, int firstByte) {
        int pos = unsigned(message.getSource()[firstByte + 6]);

        Item item = actor.inventory[pos];
        if(item == null) {
            item = new Item();
            actor.inventory[pos] = item;
        }

        item.pos = pos;
        item.imageId = nex2(message.getSource(), firstByte);
        item.quantity = nex4(message.getSource(), firstByte + 2);
    }

    private static void clearAllItems(Actor actor) {
        for(int i = 0; i < actor.inventory.length; ++i) {
            if(actor.inventory[i] != null) {
                actor.inventory[i].quantity = 0;
            }
        }
    }

    private static int calculateItemPackageSize(byte[] source, int items) {
        if(8 * items == source.length - 4) {
            return 8;
        } else {
            return 10;
        }
    }
}
