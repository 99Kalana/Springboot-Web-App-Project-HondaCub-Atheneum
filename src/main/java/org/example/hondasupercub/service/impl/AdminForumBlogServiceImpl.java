package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.BlogDTO;
import org.example.hondasupercub.dto.ForumDTO;
import org.example.hondasupercub.entity.Blog;
import org.example.hondasupercub.entity.Forum;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.AdminBlogRepo;
import org.example.hondasupercub.repo.AdminForumRepo;
import org.example.hondasupercub.repo.UserRepository;
import org.example.hondasupercub.service.AdminForumBlogService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AdminForumBlogServiceImpl implements AdminForumBlogService {

    @Autowired
    private AdminForumRepo forumRepo;

    @Autowired
    private AdminBlogRepo blogRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepo;

    // Forum Methods
    @Override
    public List<ForumDTO> getAllForums() {
        List<Forum> forums = forumRepo.findAll();
        return modelMapper.map(forums, new TypeToken<List<ForumDTO>>() {}.getType());
    }

    @Override
    public ForumDTO getForumById(int forumId) {
        Forum forum = forumRepo.findForumByForumId(forumId);
        return modelMapper.map(forum, ForumDTO.class);
    }

    @Override
    public ForumDTO createForum(ForumDTO forumDTO) {
        Forum forum = modelMapper.map(forumDTO, Forum.class);
        forum.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Optional<User> userOptional = userRepo.findById(Integer.valueOf(String.valueOf(forumDTO.getUserId())));
        if (userOptional.isPresent()) {
            forum.setUser(userOptional.get());
        }
        return modelMapper.map(forumRepo.save(forum), ForumDTO.class);
    }

    /*@Override
    public ForumDTO updateForum(int forumId, ForumDTO forumDTO) {
        Forum forum = forumRepo.findForumByForumId(forumId);
        if (forum != null) {
            modelMapper.map(forumDTO, forum);
            return modelMapper.map(forumRepo.save(forum), ForumDTO.class);
        }
        return null;
    }*/

    @Override
    public ForumDTO updateForum(int forumId, ForumDTO forumDTO) {
        Forum forum = forumRepo.findForumByForumId(forumId);
        if (forum != null) {
            // Map the DTO to the existing forum entity
            modelMapper.map(forumDTO, forum);

            //set the createdAt.
            forum.setCreatedAt(forumDTO.getCreatedAt());

            return modelMapper.map(forumRepo.save(forum), ForumDTO.class);
        }
        return null;
    }

    @Override
    public void deleteForum(int forumId) {
        forumRepo.deleteById(forumId);
    }

    /*@Override
    public List<ForumDTO> searchForums(String query) {
        List<Forum> forums = forumRepo.findByTitleContainingIgnoreCase(query);
        return modelMapper.map(forums, new TypeToken<List<ForumDTO>>() {}.getType());
    }*/

    @Override
    public List<ForumDTO> searchForums(String query, String status) {
        List<Forum> forums;
        if (query != null && !query.isEmpty() && status != null && !status.isEmpty() && !status.equals("all")) {
            Forum.ForumStatus forumStatus = Forum.ForumStatus.valueOf(status.toUpperCase());
            forums = forumRepo.findByTitleContainingIgnoreCaseAndStatus(query, forumStatus);
        } else if (query != null && !query.isEmpty()) {
            forums = forumRepo.findByTitleContainingIgnoreCase(query);
        } else if (status != null && !status.isEmpty() && !status.equals("all")) {
            Forum.ForumStatus forumStatus = Forum.ForumStatus.valueOf(status.toUpperCase());
            forums = forumRepo.findByStatus(forumStatus);
        } else {
            forums = forumRepo.findAll();
        }
        return modelMapper.map(forums, new TypeToken<List<ForumDTO>>() {}.getType());
    }

    @Override
    public List<ForumDTO> filterForumsByStatus(String status) {
        Forum.ForumStatus forumStatus = Forum.ForumStatus.valueOf(status.toUpperCase());
        List<Forum> forums = forumRepo.findByStatus(forumStatus);
        return modelMapper.map(forums, new TypeToken<List<ForumDTO>>() {}.getType());
    }

    // Blog Methods
    @Override
    public List<BlogDTO> getAllBlogs() {
        List<Blog> blogs = blogRepo.findAll();
        return modelMapper.map(blogs, new TypeToken<List<BlogDTO>>() {}.getType());
    }

    @Override
    public BlogDTO getBlogById(int blogId) {
        Blog blog = blogRepo.findBlogByBlogId(blogId);
        return modelMapper.map(blog, BlogDTO.class);
    }

   /* @Override
    public BlogDTO createBlog(BlogDTO blogDTO) {
        Blog blog = modelMapper.map(blogDTO, Blog.class);
        blog.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Optional<User> userOptional = userRepo.findById(Integer.valueOf(String.valueOf(blogDTO.getUserId())));
        if (userOptional.isPresent()) {
            blog.setUser(userOptional.get());
        }
        return modelMapper.map(blogRepo.save(blog), BlogDTO.class);
    }*/

    @Override
    public BlogDTO createBlog(BlogDTO blogDTO, MultipartFile image, int userId) throws IOException {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User  not found"));

        Blog blog = modelMapper.map(blogDTO, Blog.class);
        blog.setUser (user);

        if (image != null && !image.isEmpty()) {
            // Generate a unique filename using UUID and keep the original file extension
            String originalFilename = image.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.')); // Get the file extension
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension; // Create a unique filename

            Path filePath = Paths.get("src/main/resources/images", uniqueFileName); // Save to src/main/resources/images

            // Save the image file
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Set the image URL to match the expected format
            blog.setImageUrl("/images/" + uniqueFileName); // Set the URL to point to /images/
        }

        // Set the created date
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        blog.setCreatedAt(now.format(formatter));

        // Save the blog entity
        Blog savedBlog = blogRepo.save(blog);
        return mapBlogToDTO(savedBlog); // Convert the saved Blog entity to BlogDTO
    }

    private BlogDTO mapBlogToDTO(Blog blog) {
        return modelMapper.map(blog, BlogDTO.class);
    }

    /*@Override
    public BlogDTO updateBlog(int blogId, BlogDTO blogDTO) {
        Blog blog = blogRepo.findBlogByBlogId(blogId);
        if (blog != null) {
            modelMapper.map(blogDTO, blog);
            return modelMapper.map(blogRepo.save(blog), BlogDTO.class);
        }
        return null;
    }*/

    /*@Override
    public BlogDTO updateBlog(int blogId, BlogDTO blogDTO) {
        Blog blog = blogRepo.findBlogByBlogId(blogId);
        if (blog != null) {
            // Map the DTO to the existing blog entity
            modelMapper.map(blogDTO, blog);

            //set the createdAt.
            blog.setCreatedAt(blogDTO.getCreatedAt());

            return modelMapper.map(blogRepo.save(blog), BlogDTO.class);
        }
        return null;
    }*/

    @Override
    public BlogDTO updateBlog(int blogId, BlogDTO blogDTO, MultipartFile image) {
        Blog blog = blogRepo.findBlogByBlogId(blogId);
        if (blog != null) {
            // Log the ID before mapping
            System.out.println("Blog ID before update: " + blog.getBlogId());

            // Map the DTO to the existing blog entity, ignoring the ID
            modelMapper.typeMap(BlogDTO.class, Blog.class).addMappings(mapper -> {
                mapper.skip(Blog::setBlogId); // Ignore the ID field
                mapper.skip(Blog::setCreatedAt); // Ignore the createdAt field
            });
            modelMapper.map(blogDTO, blog);

            // Handle image update
            if (image != null && !image.isEmpty()) {
                // Generate a unique filename using UUID and keep the original file extension
                String originalFilename = image.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                Path filePath = Paths.get("src/main/resources/images", uniqueFileName);

                // Save the new image file
                try {
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    // Update the image URL in the blog entity
                    blog.setImageUrl("/images/" + uniqueFileName);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save image: " + e.getMessage());
                }
            }

            // Save the updated blog entity
            return modelMapper.map(blogRepo.save(blog), BlogDTO.class);
        }
        return null;
    }
    @Override
    public void deleteBlog(int blogId) {
        blogRepo.deleteById(blogId);
    }

    /*@Override
    public List<BlogDTO> searchBlogs(String query) {
        List<Blog> blogs = blogRepo.findBlogByTitleContainingIgnoreCase(query);
        return modelMapper.map(blogs, new TypeToken<List<BlogDTO>>() {}.getType());
    }*/

    @Override
    public List<BlogDTO> searchBlogs(String query, String status) {
        List<Blog> blogs;
        if (query != null && !query.isEmpty() && status != null && !status.isEmpty() && !status.equals("all")) {
            Blog.BlogStatus blogStatus = Blog.BlogStatus.valueOf(status.toUpperCase());
            blogs = blogRepo.findBlogByTitleContainingIgnoreCaseAndStatus(query, blogStatus);
        } else if (query != null && !query.isEmpty()) {
            blogs = blogRepo.findBlogByTitleContainingIgnoreCase(query);
        } else if (status != null && !status.isEmpty() && !status.equals("all")) {
            Blog.BlogStatus blogStatus = Blog.BlogStatus.valueOf(status.toUpperCase());
            blogs = blogRepo.findBlogByStatus(blogStatus);
        } else {
            blogs = blogRepo.findAll();
        }
        return modelMapper.map(blogs, new TypeToken<List<BlogDTO>>() {}.getType());
    }

    @Override
    public List<BlogDTO> filterBlogsByStatus(String status) {
        Blog.BlogStatus blogStatus = Blog.BlogStatus.valueOf(status.toUpperCase());
        List<Blog> blogs = blogRepo.findBlogByStatus(blogStatus);
        return modelMapper.map(blogs, new TypeToken<List<BlogDTO>>() {}.getType());
    }
}