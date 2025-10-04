package org.example;


import com.devskiller.friendly_id.FriendlyId;
import com.devskiller.friendly_id.spring.FriendlyIdConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CommentsRepository.Comment;
import org.example.PostsRepository.Post;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({DatabaseLoader.class})
public class SpringMainTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private DatabaseLoader loader;
    @Autowired
    private ObjectMapper mapper;

    private ResultActions getPostAndAssertResponse(int index) throws Exception {
        UUID postId = loader.testPosts.get(index).id;
        return mvc.perform(MockMvcRequestBuilders
            .get("/posts/{id}", postId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", equalTo(postId.toString())));
    }

    @Test
    public void shouldGetOk() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("status", equalTo("ok")))
            .andExpect(jsonPath("env", equalTo("dev")));
    }

    @Test
    public void shouldGetPostOne() throws Exception {
        getPostAndAssertResponse(0)
            .andExpect(jsonPath("content", equalTo("content 1")));
    }

    @Test
    public void shouldGetPostTwo() throws Exception {
        getPostAndAssertResponse(1)
            .andExpect(jsonPath("content", equalTo("content 2")));
    }

    @Test
    @Disabled("Ignored until FriendlyIdConfiguration.class added to @Import")
    public void shouldGetFriendlyIdPost() throws Exception {
        String postId = FriendlyId.toFriendlyId(loader.testPosts.get(0).id);
        mvc.perform(MockMvcRequestBuilders
            .get("/posts/{id}", postId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", equalTo(postId)))
                .andExpect(jsonPath("content", equalTo("content 1")));
    }

    @Test
    void shouldSavePost() throws Exception {
        Post post = new PostsRepository.Post(null, "content 3", new Date(), new Date());
        mvc.perform(MockMvcRequestBuilders
            .post("/posts")
            .content(mapper.writeValueAsString(post)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldSaveComment() throws Exception {
        UUID postId = loader.testPosts.getFirst().id;
        Comment comment = new Comment(null, postId, "comment 1", new Date(), new Date());
        mvc.perform(MockMvcRequestBuilders
            .post("/posts/{postId}/comments", comment.postId)
            .content(mapper.writeValueAsString(comment)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mvc.perform(MockMvcRequestBuilders
            .get("/posts/{id}/comments", postId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comments", iterableWithSize(1)))
                .andExpect(jsonPath("$._embedded.comments[0].postId", equalTo(postId.toString())))
                .andExpect(jsonPath("$._embedded.comments[0].content", equalTo("comment 1")));
    }
}