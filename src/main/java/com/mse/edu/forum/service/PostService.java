package com.mse.edu.forum.service;

import com.mse.edu.forum.api.generated.model.CreatePostRequest;
import com.mse.edu.forum.api.generated.model.PostResponse;
import com.mse.edu.forum.api.generated.model.UpdatePostRequest;
import com.mse.edu.forum.domain.PostEntity;
import com.mse.edu.forum.mapper.PostMapper;
import com.mse.edu.forum.repo.PostRepository;
import com.mse.edu.forum.security.CurrentUserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CurrentUserService currentUserService;

    public PostService(
            PostRepository postRepository,
            PostMapper postMapper,
            CurrentUserService currentUserService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @Transactional
    public Optional<PostResponse> findById(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setViewsCount(post.getViewsCount() + 1);
                    return postMapper.toResponse(post);
                });
    }

    @Transactional
    public PostResponse create(CreatePostRequest request) {
        PostEntity postEntity = postMapper.toEntity(request);
        postEntity.setAuthorId(currentUserService.requireCurrentUserId());

        try {
            PostEntity saved = postRepository.save(postEntity);
            return postMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Topic title must be unique");
        }
    }

    @Transactional
    public PostResponse update(Long id, UpdatePostRequest request) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!currentUserService.canEdit(post.getAuthorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this topic");
        }

        postMapper.applyUpdate(request, post);

        try {
            PostEntity saved = postRepository.save(post);
            return postMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Topic title must be unique");
        }
    }
}