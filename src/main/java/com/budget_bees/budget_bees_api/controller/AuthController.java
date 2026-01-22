package com.budget_bees.budget_bees_api.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budget_bees.budget_bees_api.dto.AuthRequest;
import com.budget_bees.budget_bees_api.dto.AuthResponse;
import com.budget_bees.budget_bees_api.security.JwtTokenProvider;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthController(
      AuthenticationManager authenticationManager,
      UserDetailsService userDetailsService,
      JwtTokenProvider jwtTokenProvider) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    final String jwt = jwtTokenProvider.generateToken(userDetails);

    return new AuthResponse(jwt);
  }
}
