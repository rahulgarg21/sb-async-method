package com.polyglot.async.service;

import com.polyglot.async.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;


public class GitHubLookupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubLookupService.class);

    private final RestTemplate restTemplate;

    public GitHubLookupService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public CompletableFuture<User> findUser(String user) {
        LOGGER.info("Looking up " + user);
        final String url = String.format("https://api.github.com/users/%s", user);
        final User results = restTemplate.getForObject(url, User.class);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted exception ", e);
        }
        return CompletableFuture.completedFuture(results);
    }


}
