package com.polyglot.async;

import com.polyglot.async.domain.User;
import com.polyglot.async.service.GitHubLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountedCompleter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

    private final GitHubLookupService gitHubLookupService;

    public AppRunner(final GitHubLookupService gitHubLookupService) {
        this.gitHubLookupService = gitHubLookupService;
    }

    @Override
    public void run(final String... args) throws Exception {

        // start the clock
        long start = System.currentTimeMillis();

        final List<String> usersNameList =
                Arrays.asList("PivotalSoftware", "CloudFoundry", "Spring-Projects", "rahulgarg21");

        final List<CompletableFuture<User>> futures =
                usersNameList.stream()
                        .map(username -> gitHubLookupService.findUser(username))
                        .collect(Collectors.toList());

        final CompletableFuture<List<User>> allDone = sequence(futures);


        // Print results, including elapsed time
        LOGGER.info("Elapsed time: " + (System.currentTimeMillis() - start));

        allDone.get().stream().forEach(user -> LOGGER.info("User: ---- >{}", user));

    }

    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );
    }

}
