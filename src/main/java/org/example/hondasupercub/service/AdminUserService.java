package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminUserService {


    void addUser(UserDTO userDTO);

    List<UserDTO> getAllUsers(String role);

    void updateUser(UserDTO userDTO);

    void deleteUser(int id);

    UserDTO getUserById(int id);

    void updateUserStatus(int id, String status);

    List<UserDTO> searchUsers(String term);

}
