package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.BlogDTO;
import org.example.hondasupercub.dto.ForumDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AdminForumBlogService {
    List<ForumDTO> getAllForums();
    ForumDTO getForumById(int forumId);
    ForumDTO createForum(ForumDTO forumDTO);
    ForumDTO updateForum(int forumId, ForumDTO forumDTO);
    void deleteForum(int forumId);
    List<ForumDTO> searchForums(String query, String status);
    List<ForumDTO> filterForumsByStatus(String status);
    List<BlogDTO> getAllBlogs();
    BlogDTO getBlogById(int blogId);
    //BlogDTO createBlog(BlogDTO blogDTO);
    BlogDTO createBlog(BlogDTO blogDTO, MultipartFile image, int userId) throws IOException;
    //BlogDTO updateBlog(int blogId, BlogDTO blogDTO);

    BlogDTO updateBlog(int blogId, BlogDTO blogDTO, MultipartFile image);
    void deleteBlog(int blogId);
    List<BlogDTO> searchBlogs(String query, String status);
    List<BlogDTO> filterBlogsByStatus(String status);
}