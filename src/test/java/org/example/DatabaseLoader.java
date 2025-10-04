package org.example;

import org.example.PostsRepository.Post;
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
    PostsRepository repository;

    List<PostsRepository.Post> testPosts = new ArrayList<>();

    @Bean
    CommandLineRunner load() {

        return args -> {
            log.info("Preloading {}", createPost("content 1"));
            log.info("Preloading {}", createPost("content 2"));
        };
    }

    PostsRepository.Post createPost(String postValue) {
        var post = repository.save(new Post(null, postValue, new Date(), new Date()));
        testPosts.add(post);
        return post;
    }
}
