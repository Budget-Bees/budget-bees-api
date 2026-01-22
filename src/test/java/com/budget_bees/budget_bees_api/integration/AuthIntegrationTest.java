package com.budget_bees.budget_bees_api.integration;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.budget_bees.budget_bees_api.common.AbstractContainerTest;
import com.budget_bees.budget_bees_api.dto.AuthRequest;
import com.budget_bees.budget_bees_api.model.User;
import com.budget_bees.budget_bees_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class AuthIntegrationTest extends AbstractContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterAndLogin() throws Exception {
        // Given: A user exists in the database
        User user = User.builder()
                .username("integration_user")
                .password(passwordEncoder.encode("secret"))
                .name("Integration User")
                .roles("ROLE_USER")
                .build();
        userRepository.save(user);

        // When: We try to login with valid credentials
        AuthRequest request = new AuthRequest("integration_user", "secret");

        // Then: We should receive a token
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        // Given: A user exists
        User user = User.builder()
                .username("integration_user")
                .password(passwordEncoder.encode("secret"))
                .name("Integration User")
                .roles("ROLE_USER")
                .build();
        userRepository.save(user);

        // When: We try to login with WRONG password
        AuthRequest request = new AuthRequest("integration_user", "wrong_password");

        // Then: We should receive 403 Forbidden (or 401 depending on config, usually
        // 403/401 for bad creds)
        // Spring Security often returns 401 or 403. Let's accept isForbidden or
        // isUnauthorized.
        // Actually DaoAuthenticationProvider throws BadCredentialsException -> 401
        // usually.
        // But let's check what default behavior is. Usually 401.
        // Let's assume 401 or 403 to be safe or check specifically.
        // In standard Spring Boot 3+ / 4: BadCredentials -> 401.

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()); // Correct credentials handling returns 401
    }
}
