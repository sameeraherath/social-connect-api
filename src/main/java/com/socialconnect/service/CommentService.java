package com.socialconnect.service;

import com.socialconnect.dto.request.CreateCommentRequest;
import com.socialconnect.dto.request.UpdateCommentRequest;
import com.socialconnect.dto.response.CommentResponse;
import com.socialconnect.dto.response.UserResponse;
import com.socialconnect.entity.Comment;
import com.socialconnect.entity.Post;
import com.socialconnect.entity.User;
import com.socialconnect.exception.ResourceNotFoundException;
import com.socialconnect.exception.UnauthorizedException;
import com.socialconnect.repository.CommentRepository;
import com.socialconnect.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public CommentResponse createComment(Long postId, String username, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        User author = userService.getEntityByUsername(username);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .build();

        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return mapToResponse(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long id, String username, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        User currentUser = userService.getEntityByUsername(username);
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this comment");
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    @Transactional
    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        User currentUser = userService.getEntityByUsername(username);
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToResponse(Comment comment) {
        UserResponse authorResponse = UserResponse.builder()
                .id(comment.getAuthor().getId())
                .username(comment.getAuthor().getUsername())
                .email(comment.getAuthor().getEmail())
                .firstName(comment.getAuthor().getFirstName())
                .lastName(comment.getAuthor().getLastName())
                .bio(comment.getAuthor().getBio())
                .profilePicture(comment.getAuthor().getProfilePicture())
                .createdAt(comment.getAuthor().getCreatedAt())
                .updatedAt(comment.getAuthor().getUpdatedAt())
                .build();

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(authorResponse)
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

