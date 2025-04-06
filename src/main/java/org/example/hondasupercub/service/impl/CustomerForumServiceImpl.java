package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.ForumDTO;
import org.example.hondasupercub.entity.Forum;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.CustomerForumRepo;
import org.example.hondasupercub.repo.CustomerForumUserRepo;
import org.example.hondasupercub.service.CustomerForumService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerForumServiceImpl implements CustomerForumService {

    private final CustomerForumRepo forumRepo;
    private final CustomerForumUserRepo userRepo;
    private final ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    public CustomerForumServiceImpl(CustomerForumRepo forumRepo, CustomerForumUserRepo userRepo, ModelMapper modelMapper) {
        this.forumRepo = forumRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ForumDTO> getAllActiveForums() {
        return forumRepo.findAllByStatus(Forum.ForumStatus.ACTIVE).stream()
                .map(this::mapForumToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ForumDTO createForum(ForumDTO forumDTO, String authorizationHeader) { // Modified to accept authorizationHeader
        Integer userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            throw new RuntimeException("Invalid or missing token");
        }

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Forum forum = modelMapper.map(forumDTO, Forum.class);
        forum.setUser(user);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        forum.setCreatedAt(now.format(formatter));

        Forum savedForum = forumRepo.save(forum);
        return mapForumToDTO(savedForum);
    }

    @Override
    public ForumDTO addResponseToForum(int forumId, String responseContent, String authorizationHeader) {
        Integer userId = extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            throw new RuntimeException("Invalid or missing token");
        }

        Forum forum = forumRepo.findById(forumId).orElseThrow(() -> new RuntimeException("Forum not found"));

        // Append the response to the existing content
        String userResponse = "User  " + userId + " Response: " + responseContent;
        forum.setContent(forum.getContent() + "\n" + userResponse); // Append response

        Forum updatedForum = forumRepo.save(forum);
        return mapForumToDTO(updatedForum);
    }

    @Override
    public ForumDTO getForumById(int forumId) {
        Forum forum = forumRepo.findById(forumId).orElseThrow(() -> new RuntimeException("Forum not found"));
        return mapForumToDTO(forum);
    }

    private ForumDTO mapForumToDTO(Forum forum) {
        return modelMapper.map(forum, ForumDTO.class);
    }

    private Integer extractUserIdFromToken(String authorizationHeader) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(authorizationHeader.substring(7)) // Remove "Bearer " prefix
                    .getBody();
            return (Integer) claims.get("userId");
        } catch (Exception e) {
            return null;
        }
    }
}