package com.budget_bees.budget_bees_api.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractContainerTest {

  private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

  static {
    POSTGRESQL_CONTAINER =
        new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("budget_wise")
            .withUsername("budget_wise")
            .withPassword("budget_wise");
    POSTGRESQL_CONTAINER.start();
  }

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
  }
}
