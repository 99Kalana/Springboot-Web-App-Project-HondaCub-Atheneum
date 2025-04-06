//CustomerHomeService.java
package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.SparePartDTO;

import java.util.List;

public interface CustomerHomeService {
    List<SparePartDTO> getFeaturedProductsWithHighRating(String authorizationHeader);
}