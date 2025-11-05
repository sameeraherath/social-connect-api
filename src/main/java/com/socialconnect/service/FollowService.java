package com.socialconnect.service;

import com.socialconnect.dto.response.UserListResponse;
import com.socialconnect.dto.response.UserResponse;
import com.socialconnect.entity.Follow;
import com.socialconnect.entity.User;
import com.socialconnect.exception.ResourceNotFoundException;
import com.socialconnect.repository.FollowRepository;
import com.socialconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public void followUser(Long userId, String username) {
        User follower = userService.getEntityByUsername(username);
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalArgumentException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollowUser(Long userId, String username) {
        User follower = userService.getEntityByUsername(username);
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new ResourceNotFoundException("Follow relationship not found"));

        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public UserListResponse getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<Follow> follows = followRepository.findByFollowing(user);
        List<UserResponse> followers = follows.stream()
                .map(follow -> mapUserToResponse(follow.getFollower()))
                .collect(Collectors.toList());
        return UserListResponse.builder()
                .users(followers)
                .totalCount((long) followers.size())
                .build();
    }

    @Transactional(readOnly = true)
    public UserListResponse getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<Follow> follows = followRepository.findByFollower(user);
        List<UserResponse> following = follows.stream()
                .map(follow -> mapUserToResponse(follow.getFollowing()))
                .collect(Collectors.toList());
        return UserListResponse.builder()
                .users(following)
                .totalCount((long) following.size())
                .build();
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return followRepository.countByFollowing(user);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return followRepository.countByFollower(user);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long userId, String username) {
        User follower = userService.getEntityByUsername(username);
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .profilePicture(user.getProfilePicture())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

