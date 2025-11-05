package com.socialconnect.controller;

import com.socialconnect.dto.response.LikeResponse;
import com.socialconnect.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<LikeResponse> likePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LikeResponse response = likeService.likePost(postId, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        likeService.unlikePost(postId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        long count = likeService.getLikeCount(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/post/{postId}/is-liked")
    public ResponseEntity<Boolean> isLiked(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isLiked = likeService.isLiked(postId, username);
        return ResponseEntity.ok(isLiked);
    }
}

