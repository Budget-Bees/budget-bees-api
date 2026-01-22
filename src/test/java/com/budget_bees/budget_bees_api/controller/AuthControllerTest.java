package com.budget_bees.budget_bees_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.budget_bees.budget_bees_api.dto.AuthRequest;
import com.budget_bees.budget_bees_api.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  private MockMvc mockMvc;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private UserDetailsService userDetailsService;

  @Mock private JwtTokenProvider jwtTokenProvider;

  @InjectMocks private AuthController authController;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  void login_shouldReturnToken() throws Exception {
    AuthRequest request = new AuthRequest("user", "password");
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
    when(userDetailsService.loadUserByUsername("user"))
        .thenReturn(
            org.springframework.security.core.userdetails.User.builder()
                .username("user")
                .password("password")
                .roles("USER")
                .build());
    when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("fake-jwt-token"));
  }
}
