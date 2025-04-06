package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.BlogDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CustomerBlogsService {
    List<BlogDTO> getAllPublishedBlogs();
    BlogDTO createBlog(BlogDTO blogDTO, MultipartFile image, int userId) throws IOException; // Single image
    BlogDTO getBlogById(int blogId);
}