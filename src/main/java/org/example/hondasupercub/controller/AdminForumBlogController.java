package org.example.hondasupercub.controller;

import jakarta.validation.Valid;
import org.example.hondasupercub.dto.BlogDTO;
import org.example.hondasupercub.dto.ForumDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.service.impl.AdminForumBlogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/adminforumblogs")
public class AdminForumBlogController {

    @Autowired
    private AdminForumBlogServiceImpl forumBlogService;

    // Forum Endpoints
    @GetMapping("/forums/getAll")
    public ResponseEntity<ResponseDTO> getAllForums() {
        List<ForumDTO> forums = forumBlogService.getAllForums();
        return new ResponseEntity<>(new ResponseDTO(200, "Forums retrieved successfully", forums), HttpStatus.OK);
    }

    @GetMapping("/forums/get/{forumId}")
    public ResponseEntity<ResponseDTO> getForumById(@PathVariable int forumId) {
        ForumDTO forum = forumBlogService.getForumById(forumId);
        if (forum != null) {
            return new ResponseEntity<>(new ResponseDTO(200, "Forum retrieved successfully", forum), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(404, "Forum not found", null), HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/forums/create")
    public ResponseEntity<ResponseDTO> createForum(@Valid @RequestBody ForumDTO forumDTO) {
        ForumDTO createdForum = forumBlogService.createForum(forumDTO);
        return new ResponseEntity<>(new ResponseDTO(201, "Forum created successfully", createdForum), HttpStatus.CREATED);
    }

    @PutMapping("/forums/update/{forumId}")
    public ResponseEntity<ResponseDTO> updateForum(@PathVariable int forumId, @Valid @RequestBody ForumDTO forumDTO) {
        ForumDTO updatedForum = forumBlogService.updateForum(forumId, forumDTO);
        if (updatedForum != null) {
            return new ResponseEntity<>(new ResponseDTO(200, "Forum updated successfully", updatedForum), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(404, "Forum not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/forums/delete/{forumId}")
    public ResponseEntity<ResponseDTO> deleteForum(@PathVariable int forumId) {
        forumBlogService.deleteForum(forumId);
        return new ResponseEntity<>(new ResponseDTO(200, "Forum deleted successfully", null), HttpStatus.OK);
    }

    /*@GetMapping("/forums/search")
    public ResponseEntity<ResponseDTO> searchForums(@RequestParam String query) {
        List<ForumDTO> forums = forumBlogService.searchForums(query);
        return new ResponseEntity<>(new ResponseDTO(200, "Forums found", forums), HttpStatus.OK);
    }*/

    /*@GetMapping("/forums/search")
    public ResponseEntity<ResponseDTO> filterForumsByStatus(@RequestParam String query, @RequestParam String status) {
        //Change the service layer to handle both query, and status.
        List<ForumDTO> forums = forumBlogService.searchForums(query, status);
        return new ResponseEntity<>(new ResponseDTO(200, "Forums filtered", forums), HttpStatus.OK);
    }*/

    @GetMapping("/forums/search")
    public ResponseEntity<ResponseDTO> filterForumsByStatus(@RequestParam(required = false) String query, @RequestParam(required = false) String status) {
        List<ForumDTO> forums = forumBlogService.searchForums(query, status);
        return new ResponseEntity<>(new ResponseDTO(200, "Forums filtered", forums), HttpStatus.OK);
    }

    /*@GetMapping("/forums/filter")
    public ResponseEntity<ResponseDTO> filterForumsByStatus(@RequestParam String status) {
        List<ForumDTO> forums = forumBlogService.filterForumsByStatus(status);
        return new ResponseEntity<>(new ResponseDTO(200, "Forums filtered", forums), HttpStatus.OK);
    }*/

    // Blog Endpoints
    @GetMapping("/blogs/getAll")
    public ResponseEntity<ResponseDTO> getAllBlogs() {
        List<BlogDTO> blogs = forumBlogService.getAllBlogs();
        return new ResponseEntity<>(new ResponseDTO(200, "Blogs retrieved successfully", blogs), HttpStatus.OK);
    }

    @GetMapping("/blogs/get/{blogId}")
    public ResponseEntity<ResponseDTO> getBlogById(@PathVariable int blogId) {
        BlogDTO blog = forumBlogService.getBlogById(blogId);
        if (blog != null) {
            return new ResponseEntity<>(new ResponseDTO(200, "Blog retrieved successfully", blog), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(404, "Blog not found", null), HttpStatus.NOT_FOUND);
        }
    }

    /*@PostMapping("/blogs/create")
    public ResponseEntity<ResponseDTO> createBlog(@RequestBody BlogDTO blogDTO) {
        BlogDTO createdBlog = forumBlogService.createBlog(blogDTO);
        return new ResponseEntity<>(new ResponseDTO(201, "Blog created successfully", createdBlog), HttpStatus.CREATED);
    }*/

    @PostMapping("/blogs/create")
    public ResponseEntity<ResponseDTO> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image, // Single image
            @RequestParam("userId") int userId) {

        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setTitle(title);
        blogDTO.setContent(content);

        try {
            BlogDTO createdBlog = forumBlogService.createBlog(blogDTO, image, userId);
            ResponseDTO responseDTO = new ResponseDTO(201, "Blog created successfully.", createdBlog);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(500, "Failed to create blog: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*@PutMapping("/blogs/update/{blogId}")
    public ResponseEntity<ResponseDTO> updateBlog(@PathVariable int blogId, @RequestBody BlogDTO blogDTO) {
        BlogDTO updatedBlog = forumBlogService.updateBlog(blogId, blogDTO);
        if (updatedBlog != null) {
            return new ResponseEntity<>(new ResponseDTO(200, "Blog updated successfully", updatedBlog), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(404, "Blog not found", null), HttpStatus.NOT_FOUND);
        }
    }*/

    @PutMapping("/blogs/update/{blogId}")
    public ResponseEntity<ResponseDTO> updateBlog(
            @PathVariable int blogId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image, // Optional image
            @RequestParam("status") String status,
            @RequestParam("userId") int userId) {

        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setTitle(title);
        blogDTO.setContent(content);
        blogDTO.setStatus(status);
        blogDTO.setUserId(userId);

        try {
            BlogDTO updatedBlog = forumBlogService.updateBlog(blogId, blogDTO, image);
            return new ResponseEntity<>(new ResponseDTO(200, "Blog updated successfully", updatedBlog), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(500, "Failed to update blog: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/blogs/delete/{blogId}")
    public ResponseEntity<ResponseDTO> deleteBlog(@PathVariable int blogId) {
        forumBlogService.deleteBlog(blogId);
        return new ResponseEntity<>(new ResponseDTO(200, "Blog deleted successfully", null), HttpStatus.OK);
    }

    /*@GetMapping("/blogs/search")
    public ResponseEntity<ResponseDTO> searchBlogs(@RequestParam String query) {
        List<BlogDTO> blogs = forumBlogService.searchBlogs(query);
        return new ResponseEntity<>(new ResponseDTO(200, "Blogs found", blogs), HttpStatus.OK);
    }*/

    /*@GetMapping("/blogs/search")
    public ResponseEntity<ResponseDTO> filterBlogsByStatus(@RequestParam String query, @RequestParam String status) {
        //Change the service layer to handle both query, and status.
        List<BlogDTO> blogs = forumBlogService.searchBlogs(query, status);
        return new ResponseEntity<>(new ResponseDTO(200, "Blogs filtered", blogs), HttpStatus.OK);
    }*/

    @GetMapping("/blogs/search")
    public ResponseEntity<ResponseDTO> filterBlogsByStatus(@RequestParam(required = false) String query, @RequestParam(required = false) String status) {
        List<BlogDTO> blogs = forumBlogService.searchBlogs(query, status);
        return new ResponseEntity<>(new ResponseDTO(200, "Blogs filtered", blogs), HttpStatus.OK);
    }

    /*@GetMapping("/blogs/filter")
    public ResponseEntity<ResponseDTO> filterBlogsByStatus(@RequestParam String status) {
        List<BlogDTO> blogs = forumBlogService.filterBlogsByStatus(status);
        return new ResponseEntity<>(new ResponseDTO(200, "Blogs filtered", blogs), HttpStatus.OK);
    }*/
}