package org.example;

import org.example.AnswerRepository.Answer;
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
    AnswerRepository repository;

    List<Answer> testAnswers = new ArrayList<>();

    @Bean
    CommandLineRunner load() {

        return args -> {
            log.info("Preloading {}", createAnswer("answer 1"));
            log.info("Preloading {}", createAnswer("answer 2"));
        };
    }

    Answer createAnswer(String answerValue) {
        var answer = repository.save(new Answer(null, answerValue, new Date(), new Date()));
        testAnswers.add(answer);
        return answer;
    }
}
