package org.example.hondasupercub.service;


import org.example.hondasupercub.dto.UserDTO;

public interface UserService {
    UserDTO searchUser(String username);

    int saveUser(UserDTO userDTO);
}
