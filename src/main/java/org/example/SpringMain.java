package org.example;

import org.example.EchoRepository.Echo;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootApplication
public class SpringMain {

    @Component
    @ConfigurationProperties
    static class EnvironmentConfig {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    
    @RestController
    public static class EchoController {
        @Autowired
        private EnvironmentConfig env;
        @Autowired
        private EchoRepository repository;

        @GetMapping("/")
        public Object index() {
            return Map.of("env", env.name, "status", "ok");
        }

        @GetMapping("/echos/{id}")
        public EntityModel<Echo> one(@PathVariable UUID id) {
            Echo echo = repository.findById(id).orElseThrow(() -> new EchoNotFoundException(id));
            return EntityModel.of(echo, //
                    linkTo(methodOn(EchoController.class).one(id)).withSelfRel(),
                    linkTo(methodOn(EchoController.class).all()).withRel("echos"));
        }

        @GetMapping("/echos")
        public CollectionModel<EntityModel<Echo>> all() {
            List<EntityModel<Echo>> echos = repository.findAll().stream()
                    .map(employee -> EntityModel.of(employee,
                            linkTo(methodOn(EchoController.class).one(employee.getId())).withSelfRel(),
                            linkTo(methodOn(EchoController.class).all()).withRel("echos")))
                    .collect(Collectors.toList());

            return CollectionModel.of(echos, linkTo(methodOn(EchoController.class).all()).withSelfRel());
        }

        @PostMapping("/echos")
        public ResponseEntity<Echo> save(@RequestBody Echo echo) {
            return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(echo));
        }
    }

    static class EchoNotFoundException extends HttpStatusCodeException {
        public EchoNotFoundException(UUID id) {
            super(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()), "Could not find " + id);
        }
    }

    @RestControllerAdvice
    static class EchoNotFoundAdvice {
        @ExceptionHandler(EchoNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String echoNotFoundHandler(EchoNotFoundException ex) {
            return ex.getMessage();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }
}