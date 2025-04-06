package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.CartDTO;
import org.example.hondasupercub.dto.CustomerProductsSellerDTO;
import org.example.hondasupercub.dto.SparePartDTO;

import java.util.List;

public interface CustomerProductsService {
    List<SparePartDTO> getAllSpareParts();
    SparePartDTO getSparePartById(int partId);
    List<SparePartDTO> searchSpareParts(String query);
    List<SparePartDTO> filterSpareParts(Double price, String category, Integer sellerId);
    CartDTO addToCart(CartDTO cartDTO);
    List<CartDTO> getCartItems(int userId);

    List<String> getAllCategories();
    List<CustomerProductsSellerDTO> getAllSellers();
}