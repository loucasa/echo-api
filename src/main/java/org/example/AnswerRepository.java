package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerRepository.Answer, UUID> {

    @Entity
    class Answer {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        final UUID id;
        final String message;
        final Date created;
        final Date modified;

        public Answer() {
            this(null, null, new Date(), new Date());
        }

        public Answer(UUID id, String message, Date created, Date modified) {
            this.id = id;
            this.message = message;
            this.created = created;
            this.modified = modified;
        }

        public UUID getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public Date getCreated() {
            return created;
        }

        public Date getModified() {
            return modified;
        }

        @Override
        public String toString() {
            return "Answer{" +
                    "id=" + id +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
