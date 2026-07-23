package simplenem12.exception;

public class InvalidMeterReadException extends RuntimeException {
    public InvalidMeterReadException(String message) {
        super(message);
    }
    public InvalidMeterReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
