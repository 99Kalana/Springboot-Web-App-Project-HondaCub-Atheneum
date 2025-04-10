package org.example.hondasupercub.service;

import com.itextpdf.text.DocumentException;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.dto.SparePartImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface SellerProductsService {

    // List<SparePartDTO> getSparePartsBySellerId(String authorizationHeader);

    SparePartDTO getSparePartById(int partId, String authorizationHeader);

    SparePartImageDTO getSparePartImageById(int imageId, String authorizationHeader);

    SparePartDTO saveSparePart(String sparePartJson, MultipartFile[] files, int sellerId, String authorizationHeader) throws IOException;

    SparePartDTO updateSparePart(int partId, String sparePartJson, MultipartFile[] files, int sellerId, String authorizationHeader) throws IOException;

    void deleteSparePart(int partId, String authorizationHeader);

    List<SparePartDTO> getSparePartsBySellerId(String authorizationHeader, String search, Integer categoryId);

    ByteArrayInputStream generateSellerProductsPdfReport(String authorizationHeader) throws DocumentException, IOException;

}