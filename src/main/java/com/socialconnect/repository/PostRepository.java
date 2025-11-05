package com.socialconnect.repository;

import com.socialconnect.entity.Post;
import com.socialconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);
    
    @Query("SELECT p FROM Post p WHERE p.author IN :followedUsers ORDER BY p.createdAt DESC")
    List<Post> findFeedPosts(@Param("followedUsers") List<User> followedUsers);
    
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderByCreatedAtDesc();
}

