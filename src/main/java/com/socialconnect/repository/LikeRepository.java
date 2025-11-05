package com.socialconnect.repository;

import com.socialconnect.entity.Like;
import com.socialconnect.entity.Post;
import com.socialconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
    long countByPost(Post post);
}

