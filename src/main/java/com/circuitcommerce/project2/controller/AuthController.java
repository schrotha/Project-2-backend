package com.circuitcommerce.project2.controller;

import javax.validation.Valid;

import com.circuitcommerce.project2.dto.AuthenticationResponse;
import com.circuitcommerce.project2.dto.LoginRequest;
import com.circuitcommerce.project2.dto.RefreshTokenRequest;
import com.circuitcommerce.project2.dto.RegisterRequest;
import com.circuitcommerce.project2.service.AuthService;
import com.circuitcommerce.project2.service.RefreshTokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {
  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  @PostMapping(value = "/signup")
  public ResponseEntity<String> signup(
        @RequestBody RegisterRequest registerRequest) {
    authService.signup(registerRequest);
    return new ResponseEntity<>("User Registration Succesful!", HttpStatus.OK);
  }

  @GetMapping(value = "/account-verification/{token}")
  public ResponseEntity<String> verifyAccount(@PathVariable String token) {
    authService.verifyAccount(token);
    return new ResponseEntity<>("Account Activated!", HttpStatus.OK);
  }

  @PostMapping(value = "/login")
  public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
    return authService.login(loginRequest);
  }

  @PostMapping(value = "/refresh/token")
  public AuthenticationResponse  refreshToken(@Valid
        @RequestBody RefreshTokenRequest refreshTokenRequest) {
    return authService.refreshToken(refreshTokenRequest);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@Valid @RequestBody
      RefreshTokenRequest refreshTokenRequest) {
    String refreshToken = refreshTokenRequest.getRefreshToken();
    refreshTokenService.deleteRefreshToken(refreshToken);
    return ResponseEntity.status(HttpStatus.OK)
        .body("Refresh Token Deleted Successfully!");
  }
}
