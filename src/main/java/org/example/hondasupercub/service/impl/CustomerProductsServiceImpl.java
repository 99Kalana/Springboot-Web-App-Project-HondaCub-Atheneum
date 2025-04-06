package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.CartDTO;
import org.example.hondasupercub.dto.CustomerProductsSellerDTO;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.entity.Cart;
import org.example.hondasupercub.entity.SparePart;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.*;
import org.example.hondasupercub.service.CustomerProductsService;
import org.example.hondasupercub.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerProductsServiceImpl implements CustomerProductsService {

    @Autowired
    private CustomerProductsSparePartRepo sparePartRepo;

    @Autowired
    private CustomerProductsCartRepo cartRepo;

    @Autowired
    private CustomerProductsCategoryRepo customerProductsCategoryRepo;

    @Autowired
    private CustomerProductsSellerRepo customerProductsSellerRepo;

    @Autowired
    private CustomerProductsUserRepo customerProductsUserRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public List<SparePartDTO> getAllSpareParts() {
        List<SparePart> spareParts = sparePartRepo.findAll();
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }

    @Override
    public SparePartDTO getSparePartById(int partId) {
        SparePart sparePart = sparePartRepo.findById(partId).orElse(null);
        return modelMapper.map(sparePart, SparePartDTO.class);
    }

    @Override
    public List<String> getAllCategories() {
        return customerProductsCategoryRepo.findAllCategoryNames();
    }

    @Override
    public List<CustomerProductsSellerDTO> getAllSellers() {
        return customerProductsSellerRepo.findSellers().stream()
                .map(user -> new CustomerProductsSellerDTO(user.getUserId(), user.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SparePartDTO> searchSpareParts(String query) {
        List<SparePart> spareParts = sparePartRepo.findByPartNameContainingIgnoreCase(query);
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }



    @Override
    public List<SparePartDTO> filterSpareParts(Double price, String category, Integer sellerId) {
        List<SparePart> spareParts = sparePartRepo.findAll().stream()
                .filter(part -> price == null || part.getPrice() <= price)
                .filter(part -> category == null || part.getCategory().getCategoryName().equalsIgnoreCase(category))
                .filter(part -> sellerId == null || (part.getSeller() != null && part.getSeller().getUserId() == sellerId))
                .collect(Collectors.toList());
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }


    @Override
    public CartDTO addToCart(CartDTO cartDTO) {
        User user = customerProductsUserRepo.findById(cartDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + cartDTO.getUserId()));

        SparePart sparePart = sparePartRepo.findById(cartDTO.getSparePartId())
                .orElseThrow(() -> new IllegalArgumentException("SparePart not found with id: " + cartDTO.getSparePartId()));

        Cart cart = modelMapper.map(cartDTO, Cart.class);

        cart.setUser(user);
        cart.setSparePart(sparePart);
        cart.setAddedAt(cartDTO.getAddedAt()); // Set addedAt

        Cart savedCart = cartRepo.save(cart);

        return modelMapper.map(savedCart, CartDTO.class);
    }

    @Override
    public List<CartDTO> getCartItems(int userId) {
        List<Cart> cartItems = cartRepo.findByUser_UserId(userId);
        return modelMapper.map(cartItems, new TypeToken<List<CartDTO>>() {}.getType());
    }

    // Method to extract user ID from Authorization header
    private Integer extractUserIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }

    // Method to extract user role from Authorization header
    private String extractUserRoleFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (String) claims.get("role");
    }
}