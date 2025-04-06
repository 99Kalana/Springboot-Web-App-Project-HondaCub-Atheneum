package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.CartDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.CustomerProductsSellerDTO;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.service.CustomerProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/products")
public class CustomerProductsController {

    @Autowired
    private CustomerProductsService customerProductsService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllProducts() {
        List<SparePartDTO> spareParts = customerProductsService.getAllSpareParts();
        ResponseDTO responseDTO = new ResponseDTO(200, "All spare parts retrieved.", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{partId}")
    public ResponseEntity<ResponseDTO> getProductById(@PathVariable int partId) {
        SparePartDTO sparePart = customerProductsService.getSparePartById(partId);
        if (sparePart != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Spare part found.", sparePart);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Spare part not found.", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchProducts(@RequestParam String query) {
        List<SparePartDTO> spareParts = customerProductsService.searchSpareParts(query);
        ResponseDTO responseDTO = new ResponseDTO(200, "Search results.", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseDTO> filterProducts(
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer sellerId) {
        List<SparePartDTO> spareParts = customerProductsService.filterSpareParts(price, category, sellerId);
        ResponseDTO responseDTO = new ResponseDTO(200, "Filtered results.", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<ResponseDTO> addToCart(@RequestBody CartDTO cartDTO) {
        CartDTO addedCartItem = customerProductsService.addToCart(cartDTO);
        if (addedCartItem != null) {
            ResponseDTO responseDTO = new ResponseDTO(201, "Item added to cart.", addedCartItem);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(400, "Failed to add item to cart.", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<ResponseDTO> getCartItems(@PathVariable int userId) {
        List<CartDTO> cartItems = customerProductsService.getCartItems(userId);
        ResponseDTO responseDTO = new ResponseDTO(200, "Cart items retrieved.", cartItems);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }



    @GetMapping("/categories")
    public ResponseEntity<ResponseDTO> getAllCategories() {
        List<String> categories = customerProductsService.getAllCategories();
        ResponseDTO responseDTO = new ResponseDTO(200, "Categories retrieved.", categories);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/sellers")
    public ResponseEntity<ResponseDTO> getAllSellers() {
        List<CustomerProductsSellerDTO> sellers = customerProductsService.getAllSellers();
        ResponseDTO responseDTO = new ResponseDTO(200, "Sellers retrieved.", sellers);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}