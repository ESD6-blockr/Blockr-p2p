package com.javarticle.spring.rest;

import io.socket.client.IO;
import io.socket.emitter.Emitter;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

public class IPSource {
    private static final List<String> PEERS = new ArrayList<>();
    private static AtomicBoolean succes = new AtomicBoolean(false);

    static {
        //PEERS.add("145.93.104.233");
    }

    public static List<String> getPeers() {
        return PEERS;
    }

    public static boolean addPeer(String peer) {
        isReachable(peer, 8080);
        try {
            Awaitility.await().atMost(2, SECONDS).untilTrue(succes);
        }
        catch(ConditionTimeoutException ex)
        {
            return false;
        }
        if ((peer != null) && validIP(peer)) {
            if (!PEERS.contains(peer)) {
                PEERS.add(peer);
            }

            return true;
        }
        return false;

    }


    public static String getRandomPeer(String requestIP) {
        if (PEERS.size() > 0) {
            String peer = PEERS.get(ThreadLocalRandom.current().nextInt(IPSource.getPeers().size()));
            isReachable(peer, 8080);
            try {
                Awaitility.await().atMost(2, SECONDS).untilTrue(succes);
                if (peer.equals(requestIP)) {
                    if (PEERS.size() == 1) {
                        return "first";
                    }
                    return getRandomPeer(requestIP);
                }
                return peer;

            } catch (ConditionTimeoutException ex) {
                PEERS.remove(peer);
                return getRandomPeer(requestIP);
            }
        }

        return "empty";
    }

    private static void isReachable(String address, int port) {
        succes.set(false);
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
                    succes.set(true);
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