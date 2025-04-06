package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.CartDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.service.CustomerCartService;
import org.example.hondasupercub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/cart")
public class CustomerCartController {

    @Autowired
    private CustomerCartService customerCartService;


    @GetMapping
    public ResponseEntity<ResponseDTO> getCartItems(@RequestHeader("Authorization") String authorizationHeader) {
        List<CartDTO> cartItems = customerCartService.getCartItems(authorizationHeader);
        ResponseDTO responseDTO = new ResponseDTO(200, "Cart items retrieved.", cartItems);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<ResponseDTO> updateCartItem(@PathVariable int cartId, @RequestBody CartDTO cartDTO) {
        CartDTO updatedCartItem = customerCartService.updateCartItem(cartId, cartDTO);
        ResponseDTO responseDTO = new ResponseDTO(200, "Cart item updated.", updatedCartItem);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<ResponseDTO> removeCartItem(@PathVariable int cartId) {
        customerCartService.removeCartItem(cartId);
        ResponseDTO responseDTO = new ResponseDTO(200, "Cart item removed.", null);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartCount(@RequestHeader("Authorization") String authorizationHeader) {
        int cartCount = customerCartService.getCartCount(authorizationHeader);
        return ResponseEntity.ok(cartCount);
    }
}