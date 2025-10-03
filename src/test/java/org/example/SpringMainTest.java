package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.PostRepository.Post;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(DatabaseLoader.class)
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
    public void shouldGetpostOne() throws Exception {
        getPostAndAssertResponse(0)
                .andExpect(jsonPath("answer", equalTo("answer 1")));
    }

    @Test
    public void shouldGetPostTwo() throws Exception {
        getPostAndAssertResponse(1)
                .andExpect(jsonPath("answer", equalTo("answer 2")));
    }

    @Test
    void shouldSavePost() throws Exception {
        Post post = new PostRepository.Post(null, "answer 3", new Date(), new Date());
        mvc.perform(MockMvcRequestBuilders
                .post("/posts")
                .content(mapper.writeValueAsString(post)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}