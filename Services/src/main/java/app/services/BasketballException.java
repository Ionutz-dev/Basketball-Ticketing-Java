package app.services;

public class BasketballException extends Exception {
    public BasketballException() {
    }

    public BasketballException(String message) {
        super(message);
    }

    public BasketballException(String message, Throwable cause) {
        super(message, cause);
    }
}
