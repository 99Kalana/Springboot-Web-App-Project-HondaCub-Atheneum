package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.BlogDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.service.CustomerBlogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/blogs")
public class CustomerBlogsController {

    @Autowired
    private CustomerBlogsService blogsService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllPublishedBlogs() {
        List<BlogDTO> blogs = blogsService.getAllPublishedBlogs();
        ResponseDTO responseDTO = new ResponseDTO(200, "Blogs retrieved successfully.", blogs);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image, // Single image
            @RequestParam("userId") int userId) {

        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setTitle(title);
        blogDTO.setContent(content);

        try {
            BlogDTO createdBlog = blogsService.createBlog(blogDTO, image, userId);
            ResponseDTO responseDTO = new ResponseDTO(201, "Blog created successfully.", createdBlog);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new ResponseDTO(500, "Failed to upload image: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(500, "Failed to create blog: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<ResponseDTO> getBlogById(@PathVariable int blogId) {
        BlogDTO blogDTO = blogsService.getBlogById(blogId);
        if (blogDTO != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Blog retrieved successfully.", blogDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Blog not found.", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }
}