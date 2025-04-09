package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.dto.SparePartImageDTO;
import org.example.hondasupercub.service.SellerProductsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/spareparts")
public class SellerProductsController {

    @Autowired
    private SellerProductsService sellerProductsService;


    /*@GetMapping
    public ResponseEntity<ResponseDTO> getSparePartsBySellerId(@RequestHeader("Authorization") String authorizationHeader) {
        List<SparePartDTO> spareParts = sellerProductsService.getSparePartsBySellerId(authorizationHeader);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK.value(), "Spare parts fetched successfully", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }*/

    @GetMapping
    public ResponseEntity<ResponseDTO> getSparePartsBySellerId(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) Integer categoryId) {
        List<SparePartDTO> spareParts = sellerProductsService.getSparePartsBySellerId(authorizationHeader, search, categoryId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK.value(), "Spare parts fetched successfully", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/{partId}")
    public ResponseEntity<ResponseDTO> getSparePartById(@PathVariable int partId, @RequestHeader("Authorization") String authorizationHeader) {
        SparePartDTO sparePart = sellerProductsService.getSparePartById(partId, authorizationHeader);
        if (sparePart == null) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Spare part not found", null), HttpStatus.NOT_FOUND);
        }
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK.value(), "Spare part fetched successfully", sparePart);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<ResponseDTO> getSparePartImageById(@PathVariable int imageId, @RequestHeader("Authorization") String authorizationHeader) {
        SparePartImageDTO sparePartImageDTO = sellerProductsService.getSparePartImageById(imageId, authorizationHeader);
        if (sparePartImageDTO == null) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Image not found", null), HttpStatus.NOT_FOUND);
        }
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK.value(), "Spare part image fetched successfully", sparePartImageDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO> saveSparePart(
            @RequestParam("sparePart") String sparePartJson,
            @RequestParam("images") MultipartFile[] files,
            @RequestParam("sellerId") int sellerId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            SparePartDTO savedSparePart = sellerProductsService.saveSparePart(sparePartJson, files, sellerId, authorizationHeader);
            ResponseDTO responseDTO = new ResponseDTO(HttpStatus.CREATED.value(), "Spare part saved successfully", savedSparePart);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{partId}")
    public ResponseEntity<ResponseDTO> updateSparePart(
            @PathVariable int partId,
            @RequestParam("sparePart") String sparePartJson,
            @RequestParam(value = "images", required = false) MultipartFile[] files,
            @RequestParam("sellerId") int sellerId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            SparePartDTO updatedSparePart = sellerProductsService.updateSparePart(partId, sparePartJson, files, sellerId, authorizationHeader); // Pass sellerId here
            ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK.value(), "Spare part updated successfully", updatedSparePart);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{partId}")
    public ResponseEntity<ResponseDTO> deleteSparePart(@PathVariable int partId, @RequestHeader("Authorization") String authorizationHeader) {
        sellerProductsService.deleteSparePart(partId, authorizationHeader);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK.value(), "Spare part deleted successfully", null);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}