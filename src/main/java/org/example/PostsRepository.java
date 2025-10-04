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
public interface PostsRepository extends JpaRepository<PostsRepository.Post, UUID> {

    @Entity
    class Post {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        final UUID id;

        final String content;

        final Date created;

        final Date modified;

        public Post() {
            this(null, null, new Date(), new Date());
        }

        public Post(UUID id, String content, Date created, Date modified) {
            this.id = id;
            this.content = content;
            this.created = created;
            this.modified = modified;
        }

        public UUID getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        public Date getCreated() {
            return created;
        }

        public Date getModified() {
            return modified;
        }

        @Override
        public String toString() {
            return "Post{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
