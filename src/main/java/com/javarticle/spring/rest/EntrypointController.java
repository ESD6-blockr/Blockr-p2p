package com.javarticle.spring.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class EntrypointController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getPeer(HttpServletRequest request) {
        return IPSource.getRandomPeer(request.getRemoteAddr());

    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<String> getPeers() {
        return IPSource.getPeers();
    }

    @RequestMapping(value = "/size", method = RequestMethod.GET)
    public int getSize() {
        return IPSource.getPeers().size();
    }

    @RequestMapping(value = "/ip", method = RequestMethod.GET)
    public String getIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ResponseEntity<Void> addPeer(HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
        if (IPSource.addPeer(request.getRemoteAddr())) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
