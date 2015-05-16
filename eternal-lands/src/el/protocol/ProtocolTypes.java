package el.protocol;

public class ProtocolTypes {
    //common
    public static final byte RAW_TEXT = 0;

    //client to server
    public static final int MOVE_TO = 1;
    public static final int SEND_PM = 2;
    public static final int SIT_DOWN = 7;
    public static final int SEND_OPENING_SCREEN = 9;
    public static final int SEND_VERSION = 10;
    public static final int HEART_BEAT = 14;
    public static final int USE_MAP_OBJECT = 16;
    public static final int MOVE_INVENTORY_ITEM = 20;
    public static final int HARVEST = 21;
    public static final int TOUCH_PLAYER = 28;
    public static final int RESPOND_TO_NPC = 29;
    public static final int MANUFACTURE_THIS = 30;
    public static final int USE_INVENTORY_ITEM = 31;
    public static final int TRADE_WITH = 32;
    public static final int ACCEPT_TRADE = 33;
    public static final int REJECT_TRADE = 34;
    public static final int EXIT_TRADE = 35;
    public static final int PUT_OBJECT_ON_TRADE = 36;
    public static final int REMOVE_OBJECT_FROM_TRADE = 37;
    public static final int GET_STORAGE_CATEGORY = 44;
    public static final int DEPOSITE_ITEM = 45;
    public static final int WITHDRAW_ITEM = 46;
    public static final int LOG_IN = 140;

    //server to client
    public static final int ADD_NEW_ACTOR = 1;
    public static final int ADD_ACTOR_COMMAND = 2;
    public static final int YOU_ARE = 3;
    public static final int NEW_MINUTE = 5;
    public static final int REMOVE_ACTOR = 6;
    public static final int CHANGE_MAP = 7;
    public static final int KILL_ALL_ACTORS = 9;
    public static final int HERE_YOUR_STATS = 18;
    public static final int HERE_YOUR_INVENTORY = 19;
    public static final int INVENTORY_ITEM_TEXT = 20; // sent when manufacture happens?
    public static final int GET_NEW_INVENTORY_ITEM = 21;
    public static final int REMOVE_ITEM_FROM_INVENTORY = 22;
    public static final int NPC_OPTIONS_LIST = 31;
    public static final int GET_TRADE_OBJECT = 35;
    public static final int GET_TRADE_ACCEPT = 36;
    public static final int GET_TRADE_REJECT = 37;
    public static final int GET_TRADE_EXIT = 38;
    public static final int REMOVE_TRADE_OBJECT = 39;
    public static final int GET_YOUR_TRADEOBJECTS = 40;
    public static final int GET_TRADE_PARTNER_NAME = 41;
    public static final int SEND_PARTIAL_STAT = 49;
    public static final int ADD_NEW_ENHANCED_ACTOR = 51;
    public static final int GET_KNOWLEDGE_LIST = 55;
    public static final int PING_REQUEST = 60;
    public static final int STORAGE_LIST = 67;
    public static final int STORAGE_ITEMS = 68;
    public static final int GET_ITEMS_COOLDOWN = 77;
    public static final int YOU_DONT_EXIST = 249;
    public static final int LOG_IN_OK = 250;
    public static final int LOG_IN_NOT_OK = 251;
}
