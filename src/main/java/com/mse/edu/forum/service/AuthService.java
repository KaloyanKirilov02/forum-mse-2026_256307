package com.mse.edu.forum.service;

import com.mse.edu.forum.api.generated.model.LoginRequest;
import com.mse.edu.forum.api.generated.model.LoginResponse;
import com.mse.edu.forum.api.generated.model.RegisterRequest;
import com.mse.edu.forum.api.generated.model.UserResponse;
import com.mse.edu.forum.security.ForumUserDetails;
import com.mse.edu.forum.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        var authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        var authenticatedSession = authenticationManager.authenticate(authenticationToken);
        var forumUser = (ForumUserDetails) authenticatedSession.getPrincipal();

        String accessToken = jwtService.createToken(
                forumUser.getId(),
                forumUser.getUsername(),
                forumUser.getDomainRole()
        );

        return new LoginResponse(accessToken, "Bearer", jwtService.getExpiresInSeconds());
    }

    public UserResponse register(RegisterRequest registrationRequest) {
        return userService.register(registrationRequest);
    }
}