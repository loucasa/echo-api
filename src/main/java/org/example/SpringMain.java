package org.example;

import org.example.PostRepository.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootApplication
public class SpringMain {

    @Component
    @Getter @Setter
    @ConfigurationProperties
    static class EnvironmentConfig {
        private String name;
    }
    
    @RestController
    public static class PostsController {
        @Autowired
        private EnvironmentConfig env;
        @Autowired
        private PostRepository repository;

        @GetMapping("/")
        public Object index() {
            return Map.of("env", env.getName(), "status", "ok");
        }

        @GetMapping("/posts/{id}")
        public EntityModel<PostRepository.Post> one(@PathVariable UUID id) {
            PostRepository.Post post = repository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
            return EntityModel.of(post, //
                    linkTo(methodOn(PostsController.class).one(id)).withSelfRel(),
                    linkTo(methodOn(PostsController.class).all()).withRel("posts"));
        }

        @GetMapping("/posts")
        public CollectionModel<EntityModel<Post>> all() {
            List<EntityModel<PostRepository.Post>> posts = repository.findAll().stream()
                    .map(post -> EntityModel.of(post,
                            linkTo(methodOn(PostsController.class).one(post.getId())).withSelfRel(),
                            linkTo(methodOn(PostsController.class).all()).withRel("posts")))
                    .collect(Collectors.toList());

            return CollectionModel.of(posts, linkTo(methodOn(PostsController.class).all()).withSelfRel());
        }

        @PostMapping("/posts")
        public ResponseEntity<Post> save(@RequestBody Post post) {
            return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(post));
        }
    }

    static class PostNotFoundException extends HttpStatusCodeException {
        public PostNotFoundException(UUID id) {
            super(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()), "Could not find " + id);
        }
    }

    @RestControllerAdvice
    static class PostNotFoundAdvice {
        @ExceptionHandler(PostNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String postNotFoundHandler(PostNotFoundException ex) {
            return ex.getMessage();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }
}