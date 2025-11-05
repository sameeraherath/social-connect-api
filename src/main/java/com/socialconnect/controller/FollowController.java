package com.socialconnect.controller;

import com.socialconnect.dto.response.UserListResponse;
import com.socialconnect.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> followUser(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        followService.followUser(userId, username);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        followService.unfollowUser(userId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<UserListResponse> getFollowers(@PathVariable Long userId) {
        UserListResponse response = followService.getFollowers(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<UserListResponse> getFollowing(@PathVariable Long userId) {
        UserListResponse response = followService.getFollowing(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Long> getFollowerCount(@PathVariable Long userId) {
        long count = followService.getFollowerCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Long> getFollowingCount(@PathVariable Long userId) {
        long count = followService.getFollowingCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{userId}/is-following")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isFollowing = followService.isFollowing(userId, username);
        return ResponseEntity.ok(isFollowing);
    }
}

