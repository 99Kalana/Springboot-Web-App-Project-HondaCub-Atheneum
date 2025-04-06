package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminForumRepo extends JpaRepository<Forum, Integer> {
    Forum findForumByForumId(int forumId);
    List<Forum> findByTitleContainingIgnoreCase(String title);
    List<Forum> findByStatus(Forum.ForumStatus status);
    List<Forum> findByTitleContainingIgnoreCaseAndStatus(String title, Forum.ForumStatus status);
}