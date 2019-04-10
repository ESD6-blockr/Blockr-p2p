package nl.blockr.p2p.controllers;

import nl.blockr.p2p.exceptions.InvalidIPException;
import nl.blockr.p2p.exceptions.NoValidatorsFoundException;
import nl.blockr.p2p.registries.IPRegistry;
import nl.blockr.p2p.utils.JSONSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class P2PController {

    private final IPRegistry registry;
    private final JSONSerializer serializer;

    public P2PController(IPRegistry registry, JSONSerializer serializer) {
        this.registry = registry;
        this.serializer = serializer;
    }

    @GetMapping(value = "/validator")
    public ResponseEntity<String> getValidator() {
        try {
            return new ResponseEntity<>(serializer.toJSON(registry.getValidator()), HttpStatus.OK);
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

    @GetMapping(value = "/peers")
    public ResponseEntity<String> getP2P() {
        return new ResponseEntity<>(serializer.toJSON(registry.getPeers()), HttpStatus.OK);
    }

    @PostMapping(value = "/peers")
    public ResponseEntity addP2P(@RequestBody String ip) {
        registry.addPeer(ip);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/status")
    public ResponseEntity getStatus() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
