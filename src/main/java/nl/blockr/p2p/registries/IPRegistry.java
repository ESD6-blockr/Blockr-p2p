package nl.blockr.p2p.registries;

import nl.blockr.p2p.exceptions.InvalidIPException;
import nl.blockr.p2p.exceptions.NoValidatorsFoundException;
import nl.blockr.p2p.models.IPAddress;
import nl.blockr.p2p.utils.IPValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class IPRegistry {

    private final IPValidator validator;

    private List<IPAddress> p2pIps;
    private List<IPAddress> validatorIps;

    @Autowired
    public IPRegistry(IPValidator validator) {
        this.validator = validator;

        p2pIps = new ArrayList<>();
        validatorIps = new ArrayList<>();
    }

    public IPAddress getValidator() throws NoValidatorsFoundException {
        if (validatorIps.isEmpty()) {
            throw new NoValidatorsFoundException("No validators available");
        }

        return validatorIps.get(new Random().nextInt(validatorIps.size()));
    }

    public void addValidator(String ip) throws InvalidIPException {
        if (!validator.isValidIp(ip)) {
            throw new InvalidIPException("Invalid IP address");
        }

        IPAddress ipAddress = new IPAddress(ip);
        int ipIndex = validatorIps.indexOf(ipAddress);

        if (ipIndex == -1) {
            validatorIps.add(ipAddress);
            return;
        }

        // Else, update ip address
        validatorIps.set(ipIndex, ipAddress);
    }

    public List<IPAddress> getPeers() {
        return this.p2pIps;
    }

    public void addPeer(String ip) {
        //TODO
    }

    private void clearOldValidatorIps() {
        // 5 minutes ago
        long fiveAgo = System.currentTimeMillis() - 5 * 60 * 1000;

        for (IPAddress ipAddress : validatorIps) {
            if (ipAddress.getDate() < fiveAgo) {
                validatorIps.remove(ipAddress);
            }
        }
    }
}