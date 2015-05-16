package el.logging;

public abstract class LoggerFactory {
    abstract protected Logger createLogger(Class c);

    public static LoggerFactory implementation = new SingleLoggerFactory(new ConsoleLogger());

    public static void setImplementation(LoggerFactory loggerFactory) {
        implementation = loggerFactory;
    }

    public static Logger logger(Class c) {
        return implementation.createLogger(c);
    }

    public static class SingleLoggerFactory extends LoggerFactory {
        private Logger logger;

        public SingleLoggerFactory(Logger logger) {
            this.logger = logger;
        }

        @Override
        protected Logger createLogger(Class c) {
            return logger;
        }
    }

    public static class ConsoleLogger implements Logger {
        @Override
        public void info(String message) {
            System.out.println(message);
        }

        @Override
        public void error(String message, Exception e) {
            System.out.println(message);
            e.printStackTrace();
        }

        @Override
        public void error(Exception e) {
            e.printStackTrace();
        }

        @Override
        public void warning(String message) {
            System.out.println(message);
        }
    }
}
