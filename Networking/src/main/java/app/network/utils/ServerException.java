package app.network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerException extends Exception {
    private static final Logger logger = LogManager.getLogger(ServerException.class);

    public ServerException(String message) {
        super(message);
        logger.debug("ServerException created: {}", message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
        logger.debug("ServerException created: {} caused by {}", message, cause.toString());
    }
}