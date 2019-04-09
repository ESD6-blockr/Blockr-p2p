package nl.blockr.p2p.controllers;

import nl.blockr.p2p.exceptions.InvalidIPException;
import nl.blockr.p2p.exceptions.NoValidatorsFoundException;
import nl.blockr.p2p.registries.IPRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class P2PController {

    private final IPRegistry registry;

    public P2PController(IPRegistry registry) {
        this.registry = registry;
    }

    @GetMapping(value = "/validator")
    public ResponseEntity<String> getValidator() {
        try {
            return new ResponseEntity<>(registry.getValidator(), HttpStatus.OK);
        } catch (NoValidatorsFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/validator")
    public ResponseEntity addValidator(@RequestBody String ip) {
        try {
            registry.addValidator(ip);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (InvalidIPException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/p2p")
    public ResponseEntity<String> getP2P() {
        return new ResponseEntity<>(registry.getP2P(), HttpStatus.OK);
    }

    @PostMapping(value = "/p2p")
    public ResponseEntity addP2P(@RequestBody String ip) {
        registry.addP2P(ip);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/status")
    public ResponseEntity getStatus() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Deprecated
    @GetMapping(value = "/")
    public String getPeer(HttpServletRequest request) {
        return registry.getRandomPeer(request.getRemoteAddr());
    }

    @Deprecated
    @GetMapping(value = "/all")
    public List<String> getPeers() {
        return registry.getPeers();
    }

    @Deprecated
    @GetMapping(value = "/size")
    public int getSize() {
        return registry.getPeers().size();
    }

    @Deprecated
    @GetMapping(value = "/ip")
    public String getIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @Deprecated
    @GetMapping(value = "/register")
    public ResponseEntity<Void> addPeer(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());

        if (registry.addPeer(request.getRemoteAddr())) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
