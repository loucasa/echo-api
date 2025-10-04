package org.example;

import com.devskiller.friendly_id.FriendlyId;
import com.devskiller.friendly_id.jackson.FriendlyIdModule;
import org.example.CommentsRepository.Comment;
import org.example.PostsRepository.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
        private PostsRepository repository;

        @GetMapping("/")
        public Object index() {
            return Map.of("env", env.getName(), "status", "ok");
        }

        @GetMapping("/posts/{id}")
        public EntityModel<Post> one(@PathVariable UUID id) {
            Post post = repository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
            return EntityModel.of(post, //
                    linkTo(methodOn(PostsController.class).one(id)).withSelfRel(),
                    linkTo(methodOn(PostsController.class).all()).withRel("posts"));
        }

        @GetMapping("/posts")
        public CollectionModel<EntityModel<Post>> all() {
            List<EntityModel<Post>> posts = repository.findAll().stream()
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

    @RestController
    public static class CommentsController {
        @Autowired
        private EnvironmentConfig env;
        @Autowired
        private CommentsRepository repository;

        @GetMapping("/posts/{postId}/comments/{commentId}")
        public EntityModel<Comment> one(@PathVariable UUID postId, @PathVariable UUID commentId) {
            Comment comment = repository.findById(postId).orElseThrow(() -> new CommentNotFoundException(postId));
            return EntityModel.of(comment, //
                    linkTo(methodOn(CommentsController.class).one(postId, commentId)).withSelfRel(),
                    linkTo(methodOn(CommentsController.class).all(postId)).withRel("comments"));
        }

        @GetMapping("/posts/{postId}/comments")
        public CollectionModel<EntityModel<Comment>> all(@PathVariable UUID postId) {
            List<EntityModel<Comment>> comments = repository.findAll().stream()
                    .map(comment -> EntityModel.of(comment,
                            linkTo(methodOn(CommentsController.class).one(comment.getPostId(), comment.getId())).withSelfRel(),
                            linkTo(methodOn(CommentsController.class).all(comment.getPostId())).withRel("comments")))
                    .collect(Collectors.toList());

            return CollectionModel.of(comments, linkTo(methodOn(CommentsController.class).all(postId)).withSelfRel());
        }

        @PostMapping("/posts/{postId}/comments")
        public ResponseEntity<Comment> save(@RequestBody Comment comment) {
            return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(comment));
        }
    }

    static class PostNotFoundException extends HttpStatusCodeException {
        public PostNotFoundException(UUID id) {
            super(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()), "Could not find " + id);
        }
    }

    static class CommentNotFoundException extends HttpStatusCodeException {
        public CommentNotFoundException(UUID id) {
            super(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()), "Could not find " + id);
        }
    }

    @RestControllerAdvice
    static class NotFoundAdvice {
        @ExceptionHandler(PostNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String postNotFoundHandler(PostNotFoundException ex) {
            return ex.getMessage();
        }

        @ExceptionHandler(CommentNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String commentNotFoundHandler(CommentNotFoundException ex) {
            return ex.getMessage();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }
}