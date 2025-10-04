package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsRepository.Comment, UUID> {

    @Entity
    @Getter
    @ToString
    @Relation(collectionRelation = "comments")
    class Comment {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        final UUID id;
        final UUID postId;
        final String content;
        final Date created;
        final Date modified;

        public Comment() {
            this(null, null, null, new Date(), new Date());
        }

        public Comment(UUID id, UUID postId, String content, Date created, Date modified) {
            this.id = id;
            this.postId = postId;
            this.content = content;
            this.created = created;
            this.modified = modified;
        }
    }
}
