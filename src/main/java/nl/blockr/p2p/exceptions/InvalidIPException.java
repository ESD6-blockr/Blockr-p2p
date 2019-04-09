package nl.blockr.p2p.exceptions;

public class InvalidIPException extends Throwable {

    public InvalidIPException(String message) {
        super("InvalidIPException:" + message);
    }
}
