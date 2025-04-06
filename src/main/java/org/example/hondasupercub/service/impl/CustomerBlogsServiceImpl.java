package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.BlogDTO;
import org.example.hondasupercub.entity.Blog;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.CustomerBlogsRepo;
import org.example.hondasupercub.repo.CustomerBlogsUserRepo;
import org.example.hondasupercub.service.CustomerBlogsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerBlogsServiceImpl implements CustomerBlogsService {

    private final CustomerBlogsRepo blogRepo;
    private final CustomerBlogsUserRepo userRepo;
    private final ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    public CustomerBlogsServiceImpl(CustomerBlogsRepo blogRepo, CustomerBlogsUserRepo userRepo, ModelMapper modelMapper) {
        this.blogRepo = blogRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<BlogDTO> getAllPublishedBlogs() {
        return blogRepo.findAllByStatus(Blog.BlogStatus.PUBLISHED).stream()
                .map(this::mapBlogToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BlogDTO createBlog(BlogDTO blogDTO, MultipartFile image, int userId) throws IOException { // Single image
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Blog blog = modelMapper.map(blogDTO, Blog.class);
        blog.setUser(user);



        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get("src/main/resources/images", fileName); // Save to src/main/resources/images
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            blog.setImageUrl("/images/" + fileName); // Serve from /images/
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        blog.setCreatedAt(now.format(formatter));

        Blog savedBlog = blogRepo.save(blog);
        return mapBlogToDTO(savedBlog);
    }

    @Override
    public BlogDTO getBlogById(int blogId) {
        Blog blog = blogRepo.findById(blogId).orElseThrow(() -> new RuntimeException("Blog not found"));
        return mapBlogToDTO(blog);
    }

    private BlogDTO mapBlogToDTO(Blog blog) {
        return modelMapper.map(blog, BlogDTO.class);
    }

    // Method to extract user ID from Authorization header
    private Integer extractUserIdFromToken(String authorizationHeader) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
            return (Integer) claims.get("userId");
        } catch (Exception e) {
            return null;
        }
    }

    // Method to extract user role from Authorization header
    private String extractUserRoleFromToken(String authorizationHeader) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
            return (String) claims.get("role");
        } catch (Exception e) {
            return null;
        }
    }
}