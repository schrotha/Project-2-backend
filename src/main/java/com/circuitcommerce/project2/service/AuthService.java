package com.circuitcommerce.project2.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import com.circuitcommerce.project2.dto.AuthenticationResponse;
import com.circuitcommerce.project2.dto.LoginRequest;
import com.circuitcommerce.project2.dto.RefreshTokenRequest;
import com.circuitcommerce.project2.dto.RegisterRequest;
import com.circuitcommerce.project2.exception.CircuitCommerceException;
import com.circuitcommerce.project2.exception.InvalidTokenException;
import com.circuitcommerce.project2.model.Cart;
import com.circuitcommerce.project2.model.NotificationEmail;
import com.circuitcommerce.project2.model.RefreshToken;
import com.circuitcommerce.project2.model.User;
import com.circuitcommerce.project2.model.VerificationToken;
import com.circuitcommerce.project2.repository.CartRepository;
import com.circuitcommerce.project2.repository.UserRepository;
import com.circuitcommerce.project2.repository.VerificationTokenRepository;
import com.circuitcommerce.project2.security.JwtProvider;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
  private final PasswordEncoder passwordEncoder;
  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final MailService mailService;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final RefreshTokenService refreshTokenService;

  public void signup(RegisterRequest registerRequest) {
    String encodedPassword =
        passwordEncoder.encode(registerRequest.getPassword());
    Cart cart = new Cart();
    User user = User.builder()
        .username(registerRequest.getUsername())
        .password(encodedPassword)
        .email(registerRequest.getEmail())
        .createdAt(Instant.now())
        .isEnabled(false)
        .cart(cart)
        .build();
    cartRepository.save(cart);
    cart.setUser(user);
    userRepository.save(user);
    cartRepository.save(cart);

    String token = generateVerificationToken(user);
    System.out.println("http://ec2-54-221-91-237.compute-1.amazonaws.com:8080/api/auth/account-verification/" + token);
    // TODO This is for production when we actually get this out the door,
    // I don't want to keep using up the emails I have and run out of emails
    // that I can send. Until then, just click the link that appears in your
    // Terminal to activate the user you created.

    // mailService.sendMail(new NotificationEmail(
    //   "Please activate your account", // Subject
    //   user.getEmail(), // Recipient
    //   // Body
    //   "Thank you for signing up with Circuit Commerce, in order to get started"
    //   + "please click the link below to activate your account."
    //   + "http://localhost:8080/api/auth/account-verification/" + token
    // ));
  }

  public void verifyAccount(String token) {
    Optional<VerificationToken> verificationToken =
        verificationTokenRepository.findByToken(token);
    verificationToken.orElseThrow(() -> new InvalidTokenException());
    fetchUserAndEnable(verificationToken.get());
  }

  @Transactional(readOnly = true)
  public User getCurrentUser() {
    String username = 
        ((org.springframework.security.core.userdetails.User)
          SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal())
            .getUsername();

    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            "Username not found: " + username));
  }

  public AuthenticationResponse login(LoginRequest loginRequest) {
    String username = loginRequest.getUsername();
    String password = loginRequest.getPassword();
    Authentication authentication =
        authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));

    Long jwtExpirationInMillis = jwtProvider.getJwtExpirationInMillis();
    SecurityContextHolder.getContext().setAuthentication(authentication);
    RefreshToken refreshToken = refreshTokenService.generateRefreshToken();
    String jwtToken = jwtProvider.generateToken(authentication);
    return AuthenticationResponse.builder()
        .authenticationToken(jwtToken)
        .username(username)
        .refreshToken(refreshToken.getToken())
        .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis))
        .build();
  }

  public boolean isLoggedIn() {
    Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

    return !(authentication instanceof AnonymousAuthenticationToken)
        && authentication.isAuthenticated();
  }

  public AuthenticationResponse refreshToken(
        RefreshTokenRequest refreshTokenRequest) {
    String refreshToken = refreshTokenRequest.getRefreshToken();
    refreshTokenService.validateRefreshToken(refreshToken);

    String username = refreshTokenRequest.getUsername();
    Long jwtExpirationInMillis = jwtProvider.getJwtExpirationInMillis();
    String jwtToken = jwtProvider.generateTokenWithUsername(username);
    return AuthenticationResponse.builder()
        .authenticationToken(jwtToken)
        .refreshToken(refreshToken)
        .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis))
        .username(username)
        .build();
  }

  private String generateVerificationToken(User user) {
    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);

    verificationTokenRepository.save(verificationToken);
    return token;
  }

  @Transactional
  private void fetchUserAndEnable(VerificationToken verificationToken) {
    @NotBlank(message = "Username is required")
    String username = verificationToken.getUser().getUsername();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() ->
          new CircuitCommerceException(
            "AuthService: Could not find user " + username));
    user.setEnabled(true);
    userRepository.save(user);
  }
}
