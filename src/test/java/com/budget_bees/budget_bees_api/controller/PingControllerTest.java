package com.budget_bees.budget_bees_api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.budget_bees.budget_bees_api.service.PingService;

@ExtendWith(MockitoExtension.class)
class PingControllerTest {

  private MockMvc mockMvc;

  @Mock private PingService pingService;

  @InjectMocks private PingController pingController;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(pingController).build();
  }

  @Test
  void ping_shouldReturnPong() throws Exception {
    when(pingService.ping()).thenReturn("Pong");

    mockMvc.perform(get("/ping")).andExpect(status().isOk()).andExpect(content().string("Pong"));
  }
}
