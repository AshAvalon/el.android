package el.actor;

public class Trade {
    public boolean isTrading;
    public boolean storageAvailable;

    public String partnersName;
    public int myAccept = 0;

    public int hisAccept = 0;
    public Item myItems[] = new Item[4 * 4];
    public Item hisItems[] = new Item[4 * 4];
}
