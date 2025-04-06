package org.example.hondasupercub.controller;

import jakarta.validation.Valid;
import org.apache.tomcat.util.http.ResponseUtil;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.UserDTO;
import org.example.hondasupercub.service.impl.AdminUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/adminuser")
public class AdminUserController {
    @Autowired
    private AdminUserServiceImpl userService;

    @PostMapping("save")
    public ResponseEntity<ResponseDTO> saveUser(@RequestBody UserDTO userDTO) {
        userService.addUser(userDTO);
        ResponseDTO responseDTO = new ResponseDTO(201, "User Saved", userDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }


    @GetMapping("getAll")
    public ResponseEntity<ResponseDTO> getAllUsers(@RequestParam(required = false) String role) {
        ResponseDTO responseDTO = new ResponseDTO(200, "User  List", userService.getAllUsers(role));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        ResponseDTO responseDTO = new ResponseDTO(200, "User Updated", userDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<ResponseDTO> deleteUser( @PathVariable("id") int id) {
        userService.deleteUser(id);
        ResponseDTO responseDTO = new ResponseDTO(200, "User Deleted", null);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable("id") int id) {
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "User Found", user);
            return new ResponseEntity<>(responseDTO, HttpStatus.FOUND);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "User Not Found", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("updateStatus/{id}/{status}")
    public ResponseEntity<ResponseDTO> updateUserStatus(@PathVariable("id") int id, @PathVariable("status") String status) {
        userService.updateUserStatus(id, status);
        ResponseDTO responseDTO = new ResponseDTO(200, "User  Status Updated", null);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<ResponseDTO> searchUsers(@RequestParam String term) {
        List<UserDTO> users = userService.searchUsers(term);
        ResponseDTO responseDTO = new ResponseDTO(200, "User  Search Results", users);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
