package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.UserDTO;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.AdminUserRepo;
import org.example.hondasupercub.service.AdminUserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private AdminUserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public void addUser(UserDTO userDTO) {
        if (userRepo.existsById(userDTO.getUserId())) {
            throw new RuntimeException("User already exists");
        }
        userRepo.save(modelMapper.map(userDTO, User.class));
    }


    @Override
    public List<UserDTO> getAllUsers(String role) {
        List<User> users;
        if (role != null && !role.equals("all")) {
            users = userRepo.findByRole(User.UserRole.valueOf(role));
        } else {
            users = userRepo.findAll();
        }
        return modelMapper.map(users, new TypeToken<List<UserDTO>>() {}.getType());
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        if (!userRepo.existsById(userDTO.getUserId())) {
            throw new RuntimeException("User does not exist");
        }
        userRepo.save(modelMapper.map(userDTO, User.class));
    }

    @Override
    public void deleteUser(int id) {
        userRepo.deleteById(id);
    }

    @Override
    public UserDTO getUserById(int id) {
        Optional<User> user = userRepo.findById(id);
        return user.map(value -> modelMapper.map(value, UserDTO.class)).orElse(null);
    }

    @Override
    public void updateUserStatus(int id, String status) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(User.UserStatus.valueOf(status));
            userRepo.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public List<UserDTO> searchUsers(String term) {
        List<User> users = userRepo.findByFullNameContainingOrEmailContaining(term, term);
        return modelMapper.map(users, new TypeToken<List<UserDTO>>() {}.getType());
    }

}
