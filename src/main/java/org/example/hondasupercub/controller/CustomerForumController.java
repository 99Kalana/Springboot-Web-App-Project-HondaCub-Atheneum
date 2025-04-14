package org.example.hondasupercub.controller;

import jakarta.validation.Valid;
import org.example.hondasupercub.dto.ForumDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.service.CustomerForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/forums")
public class CustomerForumController {

    @Autowired
    private CustomerForumService forumService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllActiveForums() {
        List<ForumDTO> forums = forumService.getAllActiveForums();
        ResponseDTO responseDTO = new ResponseDTO(200, "Forums retrieved successfully.", forums);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createForum(
            @Valid @RequestBody ForumDTO forumDTO,
            @RequestHeader("Authorization") String authorizationHeader) { // Add Authorization header
        ForumDTO createdForum = forumService.createForum(forumDTO, authorizationHeader);
        ResponseDTO responseDTO = new ResponseDTO(201, "Forum created successfully.", createdForum);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{forumId}")
    public ResponseEntity<ResponseDTO> getForumById(@PathVariable int forumId) {
        ForumDTO forumDTO = forumService.getForumById(forumId);
        if (forumDTO != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Forum retrieved successfully.", forumDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Forum not found.", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{forumId}/responses")
    public ResponseEntity<ResponseDTO> submitResponse(
            @PathVariable int forumId,
            @RequestBody String responseContent,
            @RequestHeader("Authorization") String authorizationHeader) {
        ForumDTO updatedForum = forumService.addResponseToForum(forumId, responseContent, authorizationHeader);
        ResponseDTO responseDTO = new ResponseDTO(200, "Response submitted successfully.", updatedForum);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}