package el.actor;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    public boolean openRequest;
    public List<String> categories = new ArrayList<String>(20);
    public int selectedCategory = -1;
    public Item[] items = new Item[6 * 10];
}
