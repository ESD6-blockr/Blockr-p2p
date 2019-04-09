package nl.blockr.p2p.rest;

import nl.blockr.p2p.enums.ApplicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HttpCommunicator {

    private final RestTemplate restTemplate;

    @Value("${server.port}")
    private int port;

    @Autowired
    public HttpCommunicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ApplicationStatus isReachable(String ip) {
        final String uri = String.format("http://%s:%d/status", ip, port);

        //TODO make fully async
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ApplicationStatus> result = executor.submit(() -> {
            ResponseEntity response = restTemplate.getForEntity(uri, ResponseEntity.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("bingo bango");
                return ApplicationStatus.OK;
            }
            if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                System.out.println("drukke shit");
                return ApplicationStatus.BUSY;
            }

            return ApplicationStatus.NO_RESPONSE;
        });

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ApplicationStatus.NO_RESPONSE;
        }
    }
}
