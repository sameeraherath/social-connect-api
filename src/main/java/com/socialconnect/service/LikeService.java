package com.socialconnect.service;

import com.socialconnect.dto.response.LikeResponse;
import com.socialconnect.dto.response.UserResponse;
import com.socialconnect.entity.Like;
import com.socialconnect.entity.Post;
import com.socialconnect.entity.User;
import com.socialconnect.exception.ResourceNotFoundException;
import com.socialconnect.repository.LikeRepository;
import com.socialconnect.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public LikeResponse likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        User user = userService.getEntityByUsername(username);

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new IllegalArgumentException("Post already liked");
        }

        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        like = likeRepository.save(like);
        return mapToResponse(like);
    }

    @Transactional
    public void unlikePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        User user = userService.getEntityByUsername(username);

        Like like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        likeRepository.delete(like);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return likeRepository.countByPost(post);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        User user = userService.getEntityByUsername(username);
        return likeRepository.existsByPostAndUser(post, user);
    }

    private LikeResponse mapToResponse(Like like) {
        UserResponse userResponse = UserResponse.builder()
                .id(like.getUser().getId())
                .username(like.getUser().getUsername())
                .email(like.getUser().getEmail())
                .firstName(like.getUser().getFirstName())
                .lastName(like.getUser().getLastName())
                .bio(like.getUser().getBio())
                .profilePicture(like.getUser().getProfilePicture())
                .createdAt(like.getUser().getCreatedAt())
                .updatedAt(like.getUser().getUpdatedAt())
                .build();

        return LikeResponse.builder()
                .id(like.getId())
                .postId(like.getPost().getId())
                .user(userResponse)
                .createdAt(like.getCreatedAt())
                .build();
    }
}

