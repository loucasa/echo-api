package org.example;

import org.example.AnswerRepository.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootApplication
public class SpringMain {

    @RestController
    public static class AnswerController {
        @Autowired
        private AnswerRepository repository;

        @GetMapping("/")
        public String index() {
            return "ok";
        }

        @GetMapping("/answers/{id}")
        public EntityModel<Answer> one(@PathVariable UUID id) {
            Answer answer = repository.findById(id).orElseThrow(() -> new AnswerNotFoundException(id));
            return EntityModel.of(answer, //
                    linkTo(methodOn(AnswerController.class).one(id)).withSelfRel(),
                    linkTo(methodOn(AnswerController.class).all()).withRel("answers"));
        }

        @GetMapping("/answers")
        public CollectionModel<EntityModel<Answer>> all() {
            List<EntityModel<Answer>> answers = repository.findAll().stream()
                    .map(employee -> EntityModel.of(employee,
                            linkTo(methodOn(AnswerController.class).one(employee.getId())).withSelfRel(),
                            linkTo(methodOn(AnswerController.class).all()).withRel("answers")))
                    .collect(Collectors.toList());

            return CollectionModel.of(answers, linkTo(methodOn(AnswerController.class).all()).withSelfRel());
        }

        @PostMapping("/answers")
        public ResponseEntity<Answer> save(@RequestBody Answer answer) {
            return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(answer));
        }
    }

    static class AnswerNotFoundException extends HttpStatusCodeException {
        public AnswerNotFoundException(UUID id) {
            super(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()), "Could not find " + id);
        }
    }

    @RestControllerAdvice
    static class AnswerNotFoundAdvice {
        @ExceptionHandler(AnswerNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String answerNotFoundHandler(AnswerNotFoundException ex) {
            return ex.getMessage();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }
}