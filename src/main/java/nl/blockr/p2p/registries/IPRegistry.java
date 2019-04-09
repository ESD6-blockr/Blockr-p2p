package nl.blockr.p2p.registries;

import io.socket.client.IO;
import io.socket.emitter.Emitter;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class IPRegistry {

    private final List<String> peers = new ArrayList<>();
    private static AtomicBoolean success = new AtomicBoolean(false);

    static {
        //peers.add("145.93.104.233");
    }

    public List<String> getPeers() {
        return peers;
    }

    public boolean addPeer(String peer) {
        isReachable(peer, 8080);
        try {
            Awaitility.await().atMost(2, SECONDS).untilTrue(success);
        } catch (ConditionTimeoutException ex) {
            return false;
        }
        if ((peer != null) && validIP(peer)) {
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
            isReachable(peer, 8080);
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

    private static void isReachable(String address, int port) {
        success.set(false);
        try {
            io.socket.client.Socket socket = IO.socket("http://" + address + ":" + port);
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

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    // Simple regex IP4 check
    private static boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}