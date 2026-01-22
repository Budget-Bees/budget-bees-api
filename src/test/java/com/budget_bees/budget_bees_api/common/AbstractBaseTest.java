package com.budget_bees.budget_bees_api.common;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;

// import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest()
// @AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_METHOD)
public abstract class AbstractBaseTest extends AbstractContainerTest {

  public static String password = "test123@";
  // @Autowired public MockMvc mockMvc;
}
