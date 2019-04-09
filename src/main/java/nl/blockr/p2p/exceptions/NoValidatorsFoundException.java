package nl.blockr.p2p.exceptions;

public class NoValidatorsFoundException extends Throwable {

    public NoValidatorsFoundException(String message) {
        super("NoValidatorsFoundException: " + message);
    }
}
