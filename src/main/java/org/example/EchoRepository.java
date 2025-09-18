package org.example;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Repository
public interface EchoRepository extends JpaRepository<EchoRepository.Echo, UUID> {

    @Entity
    class Echo {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        final UUID id;

        final String answer;

        final Date created;

        final Date modified;

        public Echo() {
            this(null, null, new Date(), new Date());
        }

        public Echo(UUID id, String answer, Date created, Date modified) {
            this.id = id;
            this.answer = answer;
            this.created = created;
            this.modified = modified;
        }

        public UUID getId() {
            return id;
        }

        public String getAnswer() {
            return answer;
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
                    ", answer='" + answer + '\'' +
                    '}';
        }
    }
}
