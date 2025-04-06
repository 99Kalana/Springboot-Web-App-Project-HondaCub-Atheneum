package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.CartDTO;

import java.util.List;

public interface CustomerCartService {
    List<CartDTO> getCartItems(String username);
    CartDTO updateCartItem(int cartId, CartDTO cartDTO);
    void removeCartItem(int cartId);
    int getCartCount(String username);
}