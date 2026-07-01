package com.mse.edu.forum.service;

import com.mse.edu.forum.api.generated.model.CreateUserRequest;
import com.mse.edu.forum.api.generated.model.RegisterRequest;
import com.mse.edu.forum.api.generated.model.UpdateUserRequest;
import com.mse.edu.forum.api.generated.model.UserResponse;
import com.mse.edu.forum.domain.UserEntity;
import com.mse.edu.forum.domain.UserRole;
import com.mse.edu.forum.mapper.UserMapper;
import com.mse.edu.forum.repo.UserRepository;
import com.mse.edu.forum.security.ForumUserDetails;
import com.mse.edu.forum.api.generated.model.RoleUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse create(CreateUserRequest createUserRequest) {
        UserEntity userAccount = userMapper.toEntity(createUserRequest);

        if (userRepository.existsByUsername(userAccount.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }

        if (userAccount.getEmail() != null && userRepository.existsByEmail(userAccount.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        userAccount.setPasswordHash(passwordEncoder.encode(createUserRequest.getPassword()));

        UserEntity savedUserAccount = userRepository.save(userAccount);

        return userMapper.toResponse(savedUserAccount);
    }

    @Transactional
    public UserResponse register(RegisterRequest registrationRequest) {
        UserEntity newForumUser = new UserEntity();

        String requestedUsername = userMapper.trimmed(registrationRequest.getUsername());
        String normalizedEmail = userMapper.normalizeEmail(registrationRequest.getEmail());

        if (userRepository.existsByUsername(requestedUsername)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }

        if (normalizedEmail != null && userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        newForumUser.setUsername(requestedUsername);
        newForumUser.setEmail(normalizedEmail);
        newForumUser.setRole(UserRole.USER);
        newForumUser.setPasswordHash(passwordEncoder.encode(registrationRequest.getPassword()));

        UserEntity savedUserAccount = userRepository.save(newForumUser);

        return userMapper.toResponse(savedUserAccount);
    }

    @Transactional
    public Optional<UserResponse> update(Long id, UpdateUserRequest updateUserRequest) {
        Optional<UserEntity> existingUserAccount = userRepository.findById(id);

        if (existingUserAccount.isEmpty()) {
            return Optional.empty();
        }

        UserEntity userAccount = existingUserAccount.get();

        if (!isAdmin()) {
            if (!userMapper.toApiRole(userAccount.getRole()).equals(updateUserRequest.getRole())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can change roles");
            }
        }

        String requestedUsername = userMapper.trimmed(updateUserRequest.getUsername());

        if (userRepository.existsByUsernameAndIdNot(requestedUsername, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }

        String normalizedEmail = userMapper.normalizeEmail(updateUserRequest.getEmail());

        if (normalizedEmail != null && userRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        String requestedPassword = updateUserRequest.getPassword();

        if (requestedPassword != null && !requestedPassword.isBlank()) {
            userAccount.setPasswordHash(passwordEncoder.encode(requestedPassword));
        }

        userMapper.applyUpdate(updateUserRequest, userAccount);

        UserEntity savedUserAccount = userRepository.save(userAccount);

        return Optional.of(userMapper.toResponse(savedUserAccount));
    }

    @Transactional
    public UserResponse changeRole(Long id, RoleUpdateRequest roleUpdateRequest) {
        if (!isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can change roles");
        }

        UserEntity targetUserAccount = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserRole requestedRole = userMapper.toDomainRole(roleUpdateRequest.getRole());

        targetUserAccount.setRole(requestedRole);

        UserEntity savedUserAccount = userRepository.save(targetUserAccount);

        return userMapper.toResponse(savedUserAccount);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);

        return true;
    }

    private static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof ForumUserDetails forumUser)) {
            return false;
        }

        return forumUser.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}