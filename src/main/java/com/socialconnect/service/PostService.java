package com.socialconnect.service;

import com.socialconnect.dto.request.CreatePostRequest;
import com.socialconnect.dto.request.UpdatePostRequest;
import com.socialconnect.dto.response.PostResponse;
import com.socialconnect.dto.response.UserResponse;
import com.socialconnect.entity.Post;
import com.socialconnect.entity.User;
import com.socialconnect.exception.ResourceNotFoundException;
import com.socialconnect.exception.UnauthorizedException;
import com.socialconnect.repository.LikeRepository;
import com.socialconnect.repository.PostRepository;
import com.socialconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final FollowService followService;

    @Transactional
    public PostResponse createPost(String username, CreatePostRequest request) {
        User author = userService.getEntityByUsername(username);

        Post post = Post.builder()
                .content(request.getContent())
                .author(author)
                .build();

        post = postRepository.save(post);
        return mapToResponse(post, author);
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        User currentUser = username != null ? userService.getEntityByUsername(username) : null;
        return mapToResponse(post, currentUser);
    }

    @Transactional
    public PostResponse updatePost(Long id, String username, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        User currentUser = userService.getEntityByUsername(username);
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this post");
        }

        post.setContent(request.getContent());
        post = postRepository.save(post);
        return mapToResponse(post, currentUser);
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        User currentUser = userService.getEntityByUsername(username);
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getUserPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(user);
        return posts.stream()
                .map(post -> mapToResponse(post, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getFeed(String username) {
        User currentUser = userService.getEntityByUsername(username);
        UserListResponse followingResponse = followService.getFollowing(currentUser.getId());
        List<User> followedUsers = followingResponse.getUsers().stream()
                .map(userResponse -> userRepository.findById(userResponse.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")))
                .collect(Collectors.toList());
        followedUsers.add(currentUser); // Include current user's posts
        List<Post> posts = postRepository.findFeedPosts(followedUsers);
        return posts.stream()
                .map(post -> mapToResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(String username) {
        List<Post> posts = postRepository.findAllOrderByCreatedAtDesc();
        User currentUser = username != null ? userService.getEntityByUsername(username) : null;
        return posts.stream()
                .map(post -> mapToResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    private PostResponse mapToResponse(Post post, User currentUser) {
        UserResponse authorResponse = UserResponse.builder()
                .id(post.getAuthor().getId())
                .username(post.getAuthor().getUsername())
                .email(post.getAuthor().getEmail())
                .firstName(post.getAuthor().getFirstName())
                .lastName(post.getAuthor().getLastName())
                .bio(post.getAuthor().getBio())
                .profilePicture(post.getAuthor().getProfilePicture())
                .createdAt(post.getAuthor().getCreatedAt())
                .updatedAt(post.getAuthor().getUpdatedAt())
                .build();

        long likeCount = likeRepository.countByPost(post);
        boolean isLiked = currentUser != null && likeRepository.existsByPostAndUser(post, currentUser);

        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .author(authorResponse)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}

