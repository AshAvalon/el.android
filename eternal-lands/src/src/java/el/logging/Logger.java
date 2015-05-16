package el.logging;

public interface Logger {
    public void info(String message);

    public void error(String message, Exception e);
    public void error(Exception e);

    public void warning(String message);
}
