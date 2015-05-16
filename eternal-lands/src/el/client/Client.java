package el.client;

import el.actor.*;
import el.logging.Logger;
import el.logging.LoggerFactory;
import el.map.ElMap;
import el.protocol.Message;
import el.protocol.Messages;
import el.server.MessageReceiveListener;
import el.server.ServerConnection;
import el.utils.ByteUtils;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static el.client.RawTextUtil.putRawText;
import static el.protocol.Messages.authentication;
import static el.protocol.Messages.version;
import static el.protocol.ProtocolTypes.*;

public class Client implements MessageReceiveListener {
    private static final Logger LOGGER = LoggerFactory.logger(Client.class);

    private static final String OPEN_STORAGE_OPTION = "Open storage";

    public static final byte[] VERSION = {
            0x0a, 0x0f, 0x00, 0x0a, 0x00, 0x1a, 0x00, 0x01, 0x09, 0x03, 0x00,
            0x7f, 0x00, 0x00, 0x01, 0x1f, 0x40, 0x09, 0x01, 0x00, 0x0e, 0x01, 0x00
    };
    //n, ne, e, se, s, sw, w, nw
    public static final int[] DX = {0, 1, 1, 1, 0, -1, -1, -1};
    public static final int[] DY = {1, 1, 0, -1, -1, -1, 0, 1};

    private final Heartbeat heartbeat;

    private Boolean loggedIn;
    private ServerConnection connection;
    private MoveManager moveManager;

    private final Actor actor;

    public Client(ServerConnection connection) {
        this.connection = connection;
        actor = new Actor();

        connection.addListener(this);
        connection.start();

        heartbeat = new Heartbeat(connection);
        moveManager = new MoveManager(connection);
    }

    public boolean authenticate(String username, String password) {
        LOGGER.info("Authenticating with " + username + "/" + password);

        connection.sendMessage(version(VERSION));
        connection.sendMessage(authentication(username, password));

        waitLogIn();

        return loggedIn;
    }

    public void stop() {
        heartbeat.die();
    }

    public Actor getActor() {
        return actor;
    }

    private void waitLogIn()  {
        while (connection.isConnected() && loggedIn == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }
        if(loggedIn == null) {
            loggedIn = false;
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        synchronized (actor) {
            try{
                tryProcessMessage(message);
            } catch (Exception e) {
                LOGGER.error("error while processing " + toHex(message.getSource()), e);
            }
        }
    }

    private void tryProcessMessage(Message message) {
        switch (message.getType()) {
            case LOG_IN_OK:
                onLoginOK();
                break;
            case LOG_IN_NOT_OK:
            case YOU_DONT_EXIST:
                onLoginFailed();
                break;
            case PING_REQUEST :
                onPing(message);
                break;
            case RAW_TEXT:
                onRawText(message);
                break;
            case YOU_ARE:
                onYouAre(message);
                break;
            case ADD_NEW_ACTOR:
                onAddNewActor(message);
                break;
            case ADD_NEW_ENHANCED_ACTOR:
                onAddNewEnhancedActor(message);
                break;
            case HERE_YOUR_STATS:
                onHereYourStats(message);
                break;
            case SEND_PARTIAL_STAT:
                onSendPartialStat(message);
                break;
            case HERE_YOUR_INVENTORY :
                onHereYourInventory(message);
                break;
            case GET_NEW_INVENTORY_ITEM:
                onGetNewInventoryItem(message);
                break;
            case GET_ITEMS_COOLDOWN:
                onGetItemsCooldown(message);
                break;
            case REMOVE_ITEM_FROM_INVENTORY:
                onRemoveItemFromInventory(message);
                break;
            case CHANGE_MAP:
                onChangeMap(message);
                break;
            case ADD_ACTOR_COMMAND:
                onAddActorCommand(message);
                break;
            case NEW_MINUTE:
                onNewMinute(message);
                break;
            case REMOVE_ACTOR:
                onRemoveActor(message);
                break;
            case KILL_ALL_ACTORS:
                onKillAllActors();
                break;
            case NPC_OPTIONS_LIST:
                onNpcOptionsList(message);
                break;
            case STORAGE_LIST:
                onStorageList(message);
                break;
            case STORAGE_ITEMS:
                onStorageItems(message);
                break;
            case GET_YOUR_TRADEOBJECTS:
                onGetYourTradeObjects(message);
                break;
            case GET_TRADE_PARTNER_NAME:
                onGetTradePartnerName(message);
                break;
            case GET_TRADE_OBJECT:
                onGetTradeObject(message);
                break;
            case REMOVE_TRADE_OBJECT:
                onRemoveTradeObject(message);
                break;
            case GET_TRADE_ACCEPT:
                onGetTradeAccept(message);
                break;
            case GET_TRADE_REJECT:
                onGetTradeReject(message);
                break;
            case GET_TRADE_EXIT:
                onGetTradeExit();
                break;
            case INVENTORY_ITEM_TEXT:
                onInventoryText(message);
                break;
        }
    }

    private void onLoginOK() {
        LOGGER.info("Authentication confirmed");
        loggedIn = true;
    }

    private void onLoginFailed() {
        LOGGER.info("Authentication failed");
        loggedIn = false;
        connection.stop();
    }

    private void onPing(Message message) {
        LOGGER.info("Got ping, send pong");
        connection.sendMessage(message);
    }

    private void onRawText(Message message) {
        putRawText(actor, message.getSource());

        for(Span span : actor.texts.get(actor.texts.size() - 1).spans) {
            if(span.text.contains("You started to harvest")) {
                actor.harvesting = true;
                return;
            } else if(span.text.contains("You stopped harvesting.")) {
                actor.harvesting = false;
                return;
            }
        }
        LOGGER.info(new String(message.getSource(), 4, message.getSource().length - 5));
    }


    private void onInventoryText(Message message) {
        // This might be a response to couple things
        // One of them is the manufacture response

        byte[] data = message.getSource();
        //int channel = ByteUtils.unsigned(data[3]);
        //Get the last sent text
        List<Span> spans = RawTextUtil.getSpans(data, 4, data.length);


        // Change color of the text depending on the success or failure type
        // This does not seem to be sending color information with text
        if (spans.get(0).text.contains("You successfully")
                || spans.get(0).text.contains("You started")
                || spans.get(0).text.contains("Just exp")) {
            actor.last_inventory_item_text.color = Colors.GREEN3;
        }else {
            actor.last_inventory_item_text.color = Colors.RED3;
        }

        actor.last_inventory_item_text.text = spans.get(0).text;
        LOGGER.info(new String(message.getSource(), 4, message.getSource().length - 5));
    }

    private void onYouAre(Message message) {
        byte[] bytes = message.getSource();
        actor.id= ByteUtils.nex2(bytes, 3);
        actor.actors.put(actor.id, actor);
    }

    private void onAddNewEnhancedActor(Message message) {
        byte[] bytes = message.getSource();
        int id = ByteUtils.nex2(bytes, 3);

        BaseActor baseActor = actor.actors.get(id);
        if(baseActor == null) {
            baseActor = new BaseActor();
            actor.actors.put(id, baseActor);
        }

        baseActor.id = id;
        baseActor.name = getActorName(bytes, 31);
        baseActor.nameColor = ByteUtils.unsigned(bytes[30]);
        baseActor.x = ByteUtils.nex2(bytes, 5);
        baseActor.y = ByteUtils.nex2(bytes, 7);

        if(actor.id == id) {
            moveManager.setPosition(actor.x, actor.y);
        }
    }

    private void onAddNewActor(Message message) {
        byte[] bytes = message.getSource();
        int id = ByteUtils.nex2(bytes, 3);

        BaseActor baseActor = actor.actors.get(id);
        if(baseActor == null) {
            baseActor = new BaseActor();
            actor.actors.put(id, baseActor);
        }

        baseActor.id = id;
        baseActor.name = getActorName(bytes, 20);
        baseActor.nameColor = 6;
        baseActor.x = ByteUtils.nex2(bytes, 5);
        baseActor.y = ByteUtils.nex2(bytes, 7);
    }

    private void onHereYourStats(Message message) {
        if (message.getSource().length <= 167){//taken from multiplayer.c
            LOGGER.warning("CAUTION: Possibly forged HERE_YOUR_STATS packet received.\n");
        } else {
            StatsUtil.putStats(actor, message.getSource());
        }
    }

    private void onSendPartialStat(Message message) {
        StatsUtil.putPartialStats(actor, message.getSource());
    }

    private void onHereYourInventory(Message message) {
        InventoryUtil.putInventory(actor, message);
    }

    private void onGetNewInventoryItem(Message message) {
        InventoryUtil.putItems(actor, message);
    }

    private void onGetItemsCooldown(Message message) {
        if (message.getSource().length <= 3){
            LOGGER.warning("CAUTION: Possibly forged GET_ITEMS_COOLDOWN packet received.\n");
        } else {
            InventoryUtil.getItemCooldown(actor, message);
        }
    }

    private void onRemoveItemFromInventory(Message message) {
        InventoryUtil.removeItemsFromInventory(actor, message);
    }

    private void onChangeMap(Message message) {
        String map = new String(message.getSource(), 4, message.getLength() - 3) + "a";//elm->elma
        InputStream is = getClass().getResourceAsStream(map);
        if(is != null) {
            actor.map = ElMap.fromInputStream(is);
            actor.mapPath = map;
            moveManager.setMap(actor.map);
            LOGGER.info("load map " + map);
        } else {
            LOGGER.warning("could not find map to load " + map);
        }
    }

    private void onAddActorCommand(Message message) {
        byte[] bytes = message.getSource();
        int id = ByteUtils.nex2(bytes, 3);
        BaseActor baseActor = actor.actors.get(id);

        if(baseActor == null) {
            return;
        }

        int command = ByteUtils.unsigned(bytes[5]);

        //is moving
        if(command >= 20 && command <= 27) {
            int direction = command - 20;
            baseActor.x += DX[direction];
            baseActor.y += DY[direction];

            if(actor.x == actor.moveToX && actor.y == actor.moveToY) {
                actor.moveToX = -1;
                actor.moveToY = -1;
            }

            moveManager.setPosition(actor.x, actor.y);
        }
    }

    private void onNewMinute(Message message) {
        actor.minutes = ByteUtils.nex2(message.getSource(), 3);
    }

    private void onRemoveActor(Message message) {
        byte[] bytes = message.getSource();
        int id = ByteUtils.nex2(bytes, 3);
        actor.actors.remove(id);
    }

    private void onKillAllActors() {
        actor.actors.clear();
        actor.actors.put(actor.id, actor);
    }

    private void onNpcOptionsList(Message message) {
        List<NpcOption> options = getOptionsList(message);
        if(options.size() > 0 && options.get(0).text.equals(OPEN_STORAGE_OPTION)) {
            connection.sendMessage(Messages.respondToNpc(options.get(0).toActorId, options.get(0).id));
        }
    }

    private List<NpcOption> getOptionsList(Message message) {
        List<NpcOption> options = new ArrayList<NpcOption>();

        int startOption = 3;
        while (startOption < message.getSource().length) {
            int textLength = ByteUtils.nex2(message.getSource(), startOption);

            NpcOption option = new NpcOption();
            option.text = new String(message.getSource(), startOption + 2, textLength - 1);
            option.id = ByteUtils.nex2(message.getSource(), startOption + 2 + textLength);
            option.toActorId = ByteUtils.nex2(message.getSource(), startOption + 2 + textLength + 2);

            options.add(option);
            startOption += 2 + textLength + 2 + 2;
        }

        return options;
    }

    private void onStorageList(Message message) {
        actor.storage.openRequest = !actor.trade.isTrading;
        actor.storage.selectedCategory = -1;

        StorageUtil.clearItems(actor.storage.items);
        StorageUtil.fetchCategories(actor.storage.categories, message);
    }

    private void onStorageItems(Message message) {
        StorageUtil.fetchItems(actor.storage.items, message);
    }

    private void onGetYourTradeObjects(Message message) {
        InventoryUtil.putInventory(actor, message);
        actor.trade.myAccept = 0;
        actor.trade.hisAccept = 0;
        resetItems(actor.trade.myItems);
        resetItems(actor.trade.hisItems);
        actor.trade.isTrading = true;
        actor.trade.partnersName = null;
    }

    private void onGetTradePartnerName(Message message) {
        actor.trade.storageAvailable = message.getSource()[3] != 0;
        actor.trade.partnersName = new String(message.getSource(), 4, message.getLength() - 2);
    }

    private void onGetTradeObject(Message message) {
        int pos = message.getSource()[10];
        Item items[] = message.getSource()[11] == 0 ? actor.trade.myItems : actor.trade.hisItems;
        if(items[pos] == null) {
            items[pos] = new Item();
        }

        items[pos].pos = pos;
        items[pos].imageId = ByteUtils.nex2(message.getSource(), 3);
        items[pos].quantity += ByteUtils.nex4(message.getSource(), 5);
        items[pos].type = ByteUtils.unsigned(message.getSource()[9]);
    }

    private void onRemoveTradeObject(Message message) {
        int pos = message.getSource()[7];
        Item items[] = message.getSource()[8] == 0 ? actor.trade.myItems : actor.trade.hisItems;
        items[pos].quantity -= ByteUtils.nex4(message.getSource(), 3);
    }

    private void onGetTradeAccept(Message message) {
        if(message.getSource()[3] == 0) {
            ++actor.trade.myAccept;
        } else {
            ++actor.trade.hisAccept;
        }
    }

    private void onGetTradeReject(Message message) {
        if(message.getSource()[3] == 0) {
            actor.trade.myAccept = 0;
        } else {
            actor.trade.hisAccept = 0;
        }
    }

    private List<Span> getActorName(byte[] bytes, int start) {
        int end = ByteUtils.find(bytes, start, 0);
        return RawTextUtil.getSpans(bytes, start, end);
    }

    private String toHex(byte[] bytes) {
        return new BigInteger(1, bytes).toString(16);
    }

    public void sendOpeningScreen(){
        connection.sendMessage(Messages.sendOpeningScreen());
    }

    public void sendText(String text) {
        if(text.length() > 0) {
            connection.sendMessage(Messages.rawText(text));
        }
    }

    public void useItem(int invPos) {
        connection.sendMessage(Messages.useItem(invPos));
    }

    public boolean walkTo(int x, int y) {
        boolean canMove = moveManager.moveTo(x, y);

        actor.moveToX = canMove ? x : -1;
        actor.moveToY = canMove ? y : -1;

        return canMove;
    }

    public void harvest(int itemId) {
        connection.sendMessage(Messages.harvest(itemId));
    }

    public void enter(int entrableId) {
//        List<Span> spans = new ArrayList<>(1);
//        spans.add(new Span(Colors.COLORS[Colors.BLUE1], "object id " + entrableId));
//        actor.texts.add(new Text(0, spans));
        connection.sendMessage(Messages.useMapObject(entrableId, -1));
    }

    public void touchActor(int actorId) {
        connection.sendMessage(Messages.touchActor(actorId));
    }

    public void moveItemInInventory(int fromPos, int toPos) {
        connection.sendMessage(Messages.moveInventoryItem(fromPos, toPos));
    }

    public void getStorageCategory(int category) {
        connection.sendMessage(Messages.getStorageCategory(category));
    }

    public void deposit(int itemPos, int quantity) {
        connection.sendMessage(Messages.deposit(itemPos, quantity));
    }

    public void withdraw(int itemPos, int quantity) {
        connection.sendMessage(Messages.withdraw(itemPos, quantity));
    }

    public void tradeWith(int actorId) {
        connection.sendMessage(Messages.tradeWith(actorId));
    }

    public void putObjectOnTrade(int itemPos, int quantity, boolean fromStorage) {
        connection.sendMessage(Messages.putObjectOnTrade(itemPos, quantity, fromStorage));
    }

    public void removeObjectFromTrade(int itemPos, int quantity) {
        connection.sendMessage(Messages.removeObjectFromTrade(itemPos, quantity));
    }

    public void exitTrade() {
        connection.sendMessage(Messages.exitTrade());
    }

    public void acceptTrade() {
        connection.sendMessage(Messages.acceptTrade(actor.trade.myAccept, actor.trade.hisItems));
    }

    public void rejectTrade() {
        connection.sendMessage(Messages.rejectTrade());
    }

    private void onGetTradeExit() {
        actor.trade.isTrading = false;
    }

    private void resetItems(Item[] items) {
        for (Item item : items) {
            if(item != null) {
                item.quantity = 0;
            }
        }
    }

    public void manufacture_item(Item[] manufacture_items, int mix_num){
        connection.sendMessage(Messages.manufacture(manufacture_items, mix_num));
    }

    public void sitDown(){
        connection.sendMessage(Messages.sitDown());
    }

}
