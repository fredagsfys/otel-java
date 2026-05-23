package com.example.otel.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreetingController.class)
class GreetingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void helloUsesDefaultName() throws Exception {
    mockMvc
        .perform(get("/hello"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().json("{\"message\":\"Hello, World!\"}"));
  }

  @Test
  void helloUsesProvidedName() throws Exception {
    mockMvc
        .perform(get("/hello").param("name", "Ada"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\":\"Hello, Ada!\"}"));
  }
}
