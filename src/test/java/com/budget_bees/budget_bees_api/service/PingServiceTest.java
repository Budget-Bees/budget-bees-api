package com.budget_bees.budget_bees_api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PingServiceTest {

  @InjectMocks private PingService pingService;

  @Test
  void ping_shouldReturnPong() {
    String result = pingService.ping();
    assertThat(result).isEqualTo("Pong");
  }
}
