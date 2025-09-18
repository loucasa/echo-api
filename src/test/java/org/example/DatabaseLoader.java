package org.example;

import org.example.EchoRepository.Echo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TestConfiguration
class DatabaseLoader {

    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);

    @Autowired
    EchoRepository repository;

    List<Echo> testEchos = new ArrayList<>();

    @Bean
    CommandLineRunner load() {

        return args -> {
            log.info("Preloading {}", createEcho("answer 1"));
            log.info("Preloading {}", createEcho("answer 2"));
        };
    }

    Echo createEcho(String echoValue) {
        var echo = repository.save(new Echo(null, echoValue, new Date(), new Date()));
        testEchos.add(echo);
        return echo;
    }
}
