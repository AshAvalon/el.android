package el.map;

import el.logging.Logger;
import el.logging.LoggerFactory;

import java.io.*;

public class ElMap implements Serializable {
    private static final Logger LOGGER = LoggerFactory.logger(ElMap.class);

    public String name;

    public int width;
    public int height;

    public byte[][] heightMap;

    public MapObject[] harvestables;
    public MapObject[] entrables;

    public static ElMap fromInputStream(InputStream is) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(is);
            ElMap map = new ElMap();
            byte[] b = new byte[in.readInt()];
            in.readFully(b);
            map.name = new String(b);
            map.width = in.readInt();
            map.height = in.readInt();
            map.heightMap = new byte[map.height][];
            for(int i = 0; i < map.height; ++i){
                map.heightMap[i] = new byte[map.width];
                in.readFully(map.heightMap[i]);
            }
            map.entrables = new MapObject[in.readInt()];
            for (int i = 0; i < map.entrables.length; i++) {
                map.entrables[i] = new MapObject();
                map.entrables[i].objId = in.readInt();
                map.entrables[i].x = in.readInt();
                map.entrables[i].y = in.readInt();
            }
            map.harvestables = new MapObject[in.readInt()];
            for (int i = 0; i < map.harvestables.length; i++) {
                map.harvestables[i] = new MapObject();
                map.harvestables[i].objId = in.readInt();
                map.harvestables[i].x = in.readInt();
                map.harvestables[i].y = in.readInt();
                map.harvestables[i].imgId = in.readInt();
            }
            return map;
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        } finally {
            closeQuite(in);
            closeQuite(is);
        }
    }

    private static void closeQuite(Closeable closeable) {
        if(closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
