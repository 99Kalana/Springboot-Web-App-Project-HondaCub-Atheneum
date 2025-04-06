package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerBlogsRepo extends JpaRepository<Blog, Integer> {
    List<Blog> findAllByStatus(Blog.BlogStatus status);
}