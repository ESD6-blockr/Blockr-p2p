package nl.blockr.p2p.registries;

import io.socket.client.IO;
import io.socket.emitter.Emitter;
import nl.blockr.p2p.exceptions.InvalidIPException;
import nl.blockr.p2p.exceptions.NoValidatorsFoundException;
import nl.blockr.p2p.utils.IPValidator;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class IPRegistry {

    private final IPValidator validator;

    private List<String> peers;
    private AtomicBoolean success;

    private List<String> p2pIps;
    private List<String> validatorIps;

    @Autowired
    public IPRegistry(IPValidator validator) {
        this.validator = validator;
        peers = new ArrayList<>();
        success = new AtomicBoolean(false);

        p2pIps = new ArrayList<>();
        validatorIps = new ArrayList<>();
    }

    public List<String> getPeers() {
        return peers;
    }

    public boolean addPeer(String peer) {
        isReachable(peer);
        try {
            Awaitility.await().atMost(2, SECONDS).untilTrue(success);
        } catch (ConditionTimeoutException ex) {
            return false;
        }

        if ((peer != null) && validator.isValidIp(peer)) {
            if (!peers.contains(peer)) {
                peers.add(peer);
            }

            return true;
        }
        return false;
    }

    public String getRandomPeer(String requestIP) {
        if (peers.size() > 0) {
            String peer = peers.get(ThreadLocalRandom.current().nextInt(peers.size()));
            isReachable(peer);

            try {
                Awaitility.await().atMost(2, SECONDS).untilTrue(success);

                if (peer.equals(requestIP)) {
                    if (peers.size() == 1) {
                        return "first";
                    }
                    return getRandomPeer(requestIP);
                }
                return peer;
            } catch (ConditionTimeoutException ex) {
                peers.remove(peer);
                return getRandomPeer(requestIP);
            }
        }

        return "empty";
    }

    private boolean isReachable(String address) {
        success.set(false);
        try {
            io.socket.client.Socket socket = IO.socket("http://" + address + ":8080");
            socket.on(io.socket.client.Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    socket.emit("message_isAlive", "hallo");
                }
            }).on("message_isAlive", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    success.set(true);
                    socket.disconnect();
                }
            });
            socket.connect();

            return true;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getValidator() throws NoValidatorsFoundException {
        if (validatorIps.isEmpty()) {
            throw new NoValidatorsFoundException("No validators available");
        }

        String randomValidator = validatorIps.get(new Random().nextInt(validatorIps.size()));

        if (isReachable(randomValidator)) {
            return randomValidator;
        }
        return getValidator();
    }

    public void addValidator(String ip) throws InvalidIPException {
        if (!validator.isValidIp(ip)) {
            throw new InvalidIPException("Invalid IP address");
        }

        if (isReachable(ip) && !validatorIps.contains(ip)) {
            validatorIps.add(ip);
        }
    }

    public String getP2P() {
        //TODO
        return null;
    }

    public void addP2P(String ip) {
        //TODO
    }
}