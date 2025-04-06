package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminBlogRepo extends JpaRepository<Blog, Integer> {
    Blog findBlogByBlogId(int blogId);
    List<Blog> findBlogByTitleContainingIgnoreCase(String title);
    List<Blog> findBlogByStatus(Blog.BlogStatus status);
    List<Blog> findBlogByTitleContainingIgnoreCaseAndStatus(String title, Blog.BlogStatus status);
}