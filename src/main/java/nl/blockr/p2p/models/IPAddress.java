package nl.blockr.p2p.models;

import java.util.Objects;

public class IPAddress {
    private String ip;
    private long date;

    public IPAddress(String ip) {
        this.ip = ip;
        this.date = System.currentTimeMillis();
    }

    public String getIp() {
        return ip;
    }

    public long getDate() {
        return date;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }
}
