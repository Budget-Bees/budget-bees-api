package com.budget_bees.budget_bees_api.common;

import java.io.File;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
public abstract class AbstractContainerTest {

  private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

  static {
    POSTGRESQL_CONTAINER =
        new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("budget_bees")
            .withUsername("budget_bees")
            .withPassword("budget_bees");
    POSTGRESQL_CONTAINER.start();
    runLiquibaseAgainstContainer();
  }

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
  }

  private static void runLiquibaseAgainstContainer() {
    String jdbc = POSTGRESQL_CONTAINER.getJdbcUrl();
    String user = POSTGRESQL_CONTAINER.getUsername();
    String pass = POSTGRESQL_CONTAINER.getPassword();

    log.info("========== Running Liquibase for Testcontainers DB ==========");
    log.info("JDBC URL     : {}", jdbc);
    log.info("DB Username  : {}", user);
    log.info("Working dir  : {}", System.getProperty("user.dir"));

    File apiDir = new File(System.getProperty("user.dir"));
    File dbDir = new File(apiDir.getParentFile(), "budget-bees-db");

    if (!dbDir.exists()) {
      log.warn("budget-bees-db directory not found at: {}", dbDir.getAbsolutePath());
    } else {
      log.info("budget-bees-db directory found at: {}", dbDir.getAbsolutePath());
    }

    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    File gradlew = new File(dbDir, isWindows ? "gradlew.bat" : "gradlew");

    if (!gradlew.exists()) {
      log.warn("gradlew not found at: {}", gradlew.getAbsolutePath());
    } else {
      log.info("Using gradlew script: {}", gradlew.getAbsolutePath());
    }

    ProcessBuilder pb = new ProcessBuilder(gradlew.getAbsolutePath(), "update");

    pb.environment().put("LIQUIBASE_URL", jdbc);
    pb.environment().put("LIQUIBASE_USERNAME", user);
    pb.environment().put("LIQUIBASE_PASSWORD", pass);

    pb.directory(dbDir);
    pb.redirectErrorStream(true);

    log.info("--------------------------------------------------------------");
    log.info("Executing Liquibase update command in: {}", dbDir.getAbsolutePath());
    log.info("--------------------------------------------------------------");

    try {
      Process process = pb.start();

      try (var reader =
          new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.info("[liquibase] {}", line);
        }
      }

      int exitCode = process.waitFor();
      log.info("--------------------------------------------------------------");
      log.info("Liquibase process finished with exit code: {}", exitCode);
      log.info("--------------------------------------------------------------");

      if (exitCode != 0) {
        throw new IllegalStateException("Liquibase update failed with exit code " + exitCode);
      }

      log.info("✅ Liquibase changelogs successfully applied.");
      log.info("==============================================================");

    } catch (Exception e) {
      log.error("❌ Failed to execute Liquibase update", e);
      throw new RuntimeException("Failed to run Liquibase update", e);
    }
  }
}
