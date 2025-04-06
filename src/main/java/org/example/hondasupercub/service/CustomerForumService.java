package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.ForumDTO;

import java.util.List;

public interface CustomerForumService {
    List<ForumDTO> getAllActiveForums();

    ForumDTO createForum(ForumDTO forumDTO, String authorizationHeader);
    ForumDTO getForumById(int forumId);

    ForumDTO addResponseToForum(int forumId, String responseContent, String authorizationHeader);
}