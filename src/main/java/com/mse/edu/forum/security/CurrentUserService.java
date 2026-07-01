package com.mse.edu.forum.security;

import com.mse.edu.forum.domain.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {

    public Optional<ForumUserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof ForumUserDetails forumUserDetails) {
            return Optional.of(forumUserDetails);
        }

        return Optional.empty();
    }

    public Long requireCurrentUserId() {
        return getCurrentUser()
                .map(ForumUserDetails::getId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is required"));
    }

    public UserRole requireCurrentUserRole() {
        return getCurrentUser()
                .map(ForumUserDetails::getDomainRole)
                .orElseThrow(() -> new IllegalStateException("Authenticated user is required"));
    }

    public boolean canEdit(Long authorId) {
        Long currentUserId = requireCurrentUserId();
        UserRole role = requireCurrentUserRole();

        return role == UserRole.ADMIN
                || role == UserRole.MODERATOR
                || currentUserId.equals(authorId);
    }
}