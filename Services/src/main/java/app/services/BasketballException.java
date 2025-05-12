package app.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BasketballException extends Exception {
    private static final Logger logger = LogManager.getLogger(BasketballException.class);

    public BasketballException() {
        logger.debug("Created BasketballException with no message");
    }

    public BasketballException(String message) {
        super(message);
        logger.debug("Created BasketballException: {}", message);
    }

    public BasketballException(String message, Throwable cause) {
        super(message, cause);
        logger.debug("Created BasketballException: {} caused by {}", message, cause.toString());
    }
}