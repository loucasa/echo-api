package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.AnswerRepository.Answer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    private void getAnswerAndAssertResponse(int index, String format) throws Exception {
        UUID answerId = loader.testAnswers.get(index).id;
        String expected = String.format(format, answerId.toString());
        mvc.perform(MockMvcRequestBuilders
                .get("/answer/{id}", answerId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(expected)));
    }

    @Test
    public void shouldGetOk() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("ok")));
    }

    @Test
    public void shouldGetAnswerOne() throws Exception {
        getAnswerAndAssertResponse(0, "{\"id\":\"%s\",\"response\":\"answer 1\"}");
    }

    @Test
    public void shouldGetAnswerTwo() throws Exception {
        getAnswerAndAssertResponse(1, "{\"id\":\"%s\",\"response\":\"answer 2\"}");
    }

    @Test
    void shouldSaveAnswer() throws Exception {
        Answer answer = new Answer(null, "answer 3", new Date(), new Date());
        mvc.perform(MockMvcRequestBuilders
                .post("/answer")
                .content(mapper.writeValueAsString(answer)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}