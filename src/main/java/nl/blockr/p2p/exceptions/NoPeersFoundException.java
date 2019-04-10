package nl.blockr.p2p.exceptions;

public class NoPeersFoundException extends Throwable {

    public NoPeersFoundException(String message) {
        super("NoPeersFoundException: " + message);
    }
}
