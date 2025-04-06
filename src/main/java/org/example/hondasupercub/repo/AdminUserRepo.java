package org.example.hondasupercub.repo;

import org.example.hondasupercub.dto.UserDTO;
import org.example.hondasupercub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserRepo extends JpaRepository<User, Integer> {

    List<User> findByRole(User.UserRole role);

    List<User> findByFullNameContainingOrEmailContaining(String fullName, String email);

}
