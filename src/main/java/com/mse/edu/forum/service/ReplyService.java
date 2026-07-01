package com.mse.edu.forum.service;

import com.mse.edu.forum.api.generated.model.CreateReplyRequest;
import com.mse.edu.forum.api.generated.model.ReplyResponse;
import com.mse.edu.forum.api.generated.model.UpdateReplyRequest;
import com.mse.edu.forum.domain.ReplyEntity;
import com.mse.edu.forum.mapper.ReplyMapper;
import com.mse.edu.forum.repo.PostRepository;
import com.mse.edu.forum.repo.ReplyRepository;
import com.mse.edu.forum.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final ReplyMapper replyMapper;
    private final CurrentUserService currentUserService;

    public ReplyService(
            ReplyRepository replyRepository,
            PostRepository postRepository,
            ReplyMapper replyMapper,
            CurrentUserService currentUserService) {
        this.replyRepository = replyRepository;
        this.postRepository = postRepository;
        this.replyMapper = replyMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<ReplyResponse> findByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        return replyRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(replyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ReplyResponse> findById(Long id) {
        return replyRepository.findById(id).map(replyMapper::toResponse);
    }

    @Transactional
    public ReplyResponse create(Long postId, CreateReplyRequest request) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        ReplyEntity entity = replyMapper.toEntity(request, postId);
        entity.setAuthorId(currentUserService.requireCurrentUserId());

        ReplyEntity saved = replyRepository.save(entity);

        return replyMapper.toResponse(saved);
    }

    @Transactional
    public ReplyResponse update(Long id, UpdateReplyRequest request) {
        ReplyEntity reply = replyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reply not found"));

        if (!currentUserService.canEdit(reply.getAuthorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this reply");
        }

        replyMapper.applyUpdate(request, reply);

        ReplyEntity saved = replyRepository.save(reply);

        return replyMapper.toResponse(saved);
    }
}