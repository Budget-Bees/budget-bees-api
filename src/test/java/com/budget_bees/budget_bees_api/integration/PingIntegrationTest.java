package com.budget_bees.budget_bees_api.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.budget_bees.budget_bees_api.common.AbstractContainerTest;
import com.budget_bees.budget_bees_api.model.User;
import com.budget_bees.budget_bees_api.repository.UserRepository;
import com.budget_bees.budget_bees_api.security.JwtTokenProvider;
import com.budget_bees.budget_bees_api.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class PingIntegrationTest extends AbstractContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isForbidden()); // JWT filter returns 403 for unauthorized access to protected
                                                    // endpoints by default
        // JwtFilter might let it through to EntryPoint which sends 403 by default for
        // anonymous?
        // Actually if permitAll() is not set, it requires authentication.
        // Standard behavior: 403 Forbidden for missing credentials in simple setups or
        // 401.
        // Let's assert isForbidden() first.
    }

    @Test
    void shouldReturnPongWithValidToken() throws Exception {
        // Given: A user exists
        User user = User.builder()
                .username("ping_user")
                .password(passwordEncoder.encode("pass"))
                .name("Ping User")
                .roles("ROLE_USER")
                .build();
        userRepository.save(user);

        // And: We generate a valid token
        String token = jwtTokenProvider.generateToken(userDetailsService.loadUserByUsername("ping_user"));

        // When: We call /ping with the token
        mockMvc.perform(get("/ping")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pong")));
    }
}
