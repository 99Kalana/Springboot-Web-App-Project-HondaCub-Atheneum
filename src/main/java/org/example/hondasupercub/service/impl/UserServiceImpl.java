package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.UserDTO;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.UserRepository;
import org.example.hondasupercub.service.UserService;
import org.example.hondasupercub.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user){
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return authorities;
    }


    public UserDTO loadUserDetailsByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(username);
        return modelMapper.map(user, UserDTO.class);
    }


    @Override
    public UserDTO searchUser(String username){
        if (userRepository.existsByEmail(username)){
            User user = userRepository.findByEmail(username);
            return modelMapper.map(user, UserDTO.class);
        }else {
            return null;
        }
    }

    @Override
    public int saveUser (UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;
        } else {
            try {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                userRepository.save(modelMapper.map(userDTO, User.class));
                return VarList.Created;
            } catch (Exception e) {
                // Log the exception
                System.err.println("Error saving user: " + e.getMessage());
                return VarList.Bad_Gateway; // Or another appropriate error code
            }
        }
    }



}
