package el.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static void closeQuite(Closeable closeable) {
        if(closeable != null) {
            try {closeable.close(); } catch (IOException ignored) { }
        }
    }
}
