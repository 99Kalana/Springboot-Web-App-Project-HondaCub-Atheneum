package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.CartDTO;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.entity.Cart;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.CustomerCartRepo;
import org.example.hondasupercub.repo.CustomerCartSparePartRepo;
import org.example.hondasupercub.repo.CustomerCartUserRepo;
import org.example.hondasupercub.service.CustomerCartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerCartServiceImpl implements CustomerCartService {

    @Autowired
    private CustomerCartRepo cartRepo;

    @Autowired
    private CustomerCartUserRepo userRepo;

    @Autowired
    private CustomerCartSparePartRepo sparePartRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public List<CartDTO> getCartItems(String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            return List.of();
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            return List.of();
        }

        List<Cart> cartItems = cartRepo.findByUser(user);
        return cartItems.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    cartDTO.setSparePart(modelMapper.map(cart.getSparePart(), SparePartDTO.class)); // Map sparepart
                    return cartDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO updateCartItem(int cartId, CartDTO cartDTO) {
        Cart cart = cartRepo.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setQuantity(cartDTO.getQuantity());
            cartRepo.save(cart);
            return modelMapper.map(cart, CartDTO.class);
        }
        return null;
    }

    @Override
    public void removeCartItem(int cartId) {
        cartRepo.deleteById(cartId);
    }

    @Override
    public int getCartCount(String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        if (email == null) {
            return 0;
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            return 0;
        }
        return cartRepo.countByUser(user);
    }

    // Method to extract email from Authorization header
    private String extractEmailFromToken(String authorizationHeader) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
            return claims.getSubject(); // Extract email from subject
        } catch (Exception e) {
            return null;
        }
    }

    // Method to extract user role from Authorization header
    private String extractUserRoleFromToken(String authorizationHeader) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
            return (String) claims.get("role");
        } catch (Exception e) {
            return null;
        }
    }
}