package nl.blockr.p2p.controllers;

import nl.blockr.p2p.registries.IPRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class P2PController {

    private final IPRegistry registry;

    public P2PController(IPRegistry registry) {
        this.registry = registry;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getPeer(HttpServletRequest request) {
        return registry.getRandomPeer(request.getRemoteAddr());
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<String> getPeers() {
        return registry.getPeers();
    }

    @RequestMapping(value = "/size", method = RequestMethod.GET)
    public int getSize() {
        return registry.getPeers().size();
    }

    @RequestMapping(value = "/ip", method = RequestMethod.GET)
    public String getIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ResponseEntity<Void> addPeer(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
        if (registry.addPeer(request.getRemoteAddr())) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
