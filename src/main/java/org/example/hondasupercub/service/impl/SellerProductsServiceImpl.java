package org.example.hondasupercub.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.dto.SparePartImageDTO;
import org.example.hondasupercub.entity.Category;
import org.example.hondasupercub.entity.SparePart;
import org.example.hondasupercub.entity.SparePartImage;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.AdminCategoryRepo;
import org.example.hondasupercub.repo.SparePartImageRepo;
import org.example.hondasupercub.repo.SparePartRepo;
import org.example.hondasupercub.repo.UserRepository;
import org.example.hondasupercub.service.SellerProductsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SellerProductsServiceImpl implements SellerProductsService {

    @Autowired
    private SparePartRepo sparePartRepo;

    @Autowired
    private SparePartImageRepo sparePartImageRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AdminCategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    private final Path imageStorageLocation;

    @Autowired
    public SellerProductsServiceImpl(@Value("${upload.directory}") String uploadDirectory) {
        this.imageStorageLocation = Paths.get(uploadDirectory);
    }

    /*@Override
    public List<SparePartDTO> getSparePartsBySellerId(String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        List<SparePart> spareParts = sparePartRepo.findBySeller_UserId(sellerId);
        return spareParts.stream().map(sparePart -> modelMapper.map(sparePart, SparePartDTO.class)).collect(Collectors.toList());
    }*/

    @Override
    public SparePartDTO getSparePartById(int partId, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        SparePart sparePart = sparePartRepo.findByPartIdAndSeller_UserId(partId, sellerId);
        return modelMapper.map(sparePart, SparePartDTO.class);
    }

    @Override
    public List<SparePartDTO> getSparePartsBySellerId(String authorizationHeader, String search, Integer categoryId) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        List<SparePart> spareParts;

        if (search != null && !search.trim().isEmpty() && categoryId != null) {
            spareParts = sparePartRepo.findBySeller_UserIdAndCategory_CategoryIdAndPartNameOrPartId(sellerId, categoryId, search);
        } else if (search != null && !search.trim().isEmpty()) {
            spareParts = sparePartRepo.findBySeller_UserIdAndPartNameOrPartId(sellerId, search);
        } else if (categoryId != null) {
            spareParts = sparePartRepo.findBySeller_UserIdAndCategory_CategoryId(sellerId, categoryId);
        } else {
            spareParts = sparePartRepo.findBySeller_UserId(sellerId);
        }

        return spareParts.stream().map(sparePart -> modelMapper.map(sparePart, SparePartDTO.class)).collect(Collectors.toList());
    }

    @Override
    public SparePartImageDTO getSparePartImageById(int imageId, String authorizationHeader) {
        SparePartImage sparePartImage = sparePartImageRepo.findById(imageId).orElse(null);
        return modelMapper.map(sparePartImage, SparePartImageDTO.class);
    }

    @Override
    public SparePartDTO saveSparePart(String sparePartJson, MultipartFile[] files, int sellerId, String authorizationHeader) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SparePartDTO sparePartDTO = objectMapper.readValue(sparePartJson, SparePartDTO.class);

        SparePart sparePart = modelMapper.map(sparePartDTO, SparePart.class);
        User seller = userRepo.findById(sellerId).orElse(null);
        Category category = categoryRepo.findById(sparePartDTO.getCategoryId()).orElse(null);

        sparePart.setSeller(seller);
        sparePart.setCategory(category);
        SparePart savedSparePart = sparePartRepo.save(sparePart);

        List<SparePartImage> savedImages = saveSparePartImages(savedSparePart, files);
        savedSparePart.setImages(savedImages); // Set images to the model

        return modelMapper.map(savedSparePart, SparePartDTO.class);
    }

    private List<SparePartImage> saveSparePartImages(SparePart sparePart, MultipartFile[] images) throws IOException {
        if (images == null || images.length == 0) {
            return new ArrayList<>();
        }

        List<SparePartImage> savedImages = new ArrayList<>();
        for (MultipartFile image : images) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path targetLocation = imageStorageLocation.resolve(fileName);
            Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            SparePartImage sparePartImage = new SparePartImage();
            sparePartImage.setSparePart(sparePart);
            sparePartImage.setImageUrl(fileName); // Store filename, not full path

            savedImages.add(sparePartImageRepo.save(sparePartImage));
        }
        return savedImages;
    }

    /*@Override
    public SparePartDTO updateSparePart(int partId, String sparePartJson, MultipartFile[] files, int sellerId, String authorizationHeader) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SparePartDTO sparePartDTO = objectMapper.readValue(sparePartJson, SparePartDTO.class);

        SparePart sparePart = modelMapper.map(sparePartDTO, SparePart.class);
        SparePart existingSparePart = sparePartRepo.findById(partId).orElse(null);

        if (existingSparePart != null) {
            modelMapper.map(sparePart, existingSparePart);
            existingSparePart.setCategory(categoryRepo.findById(sparePartDTO.getCategoryId()).orElse(null));

            SparePart updatedSparePart = sparePartRepo.save(existingSparePart);

            // Delete old images and add new ones
            sparePartImageRepo.findBySparePart_PartId(updatedSparePart.getPartId()).forEach(sparePartImageRepo::delete);
            updatedSparePart.setImages(saveSparePartImages(updatedSparePart, files));

            return modelMapper.map(updatedSparePart, SparePartDTO.class);
        }
        return null;
    }*/

    @Override
    public SparePartDTO updateSparePart(int partId, String sparePartJson, MultipartFile[] files, int sellerId, String authorizationHeader) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SparePartDTO sparePartDTO = objectMapper.readValue(sparePartJson, SparePartDTO.class);

        SparePart existingSparePart = sparePartRepo.findByPartIdAndSeller_UserId(partId, sellerId);

        if (existingSparePart != null) {

            existingSparePart.setPartName(sparePartDTO.getPartName());
            existingSparePart.setPrice(sparePartDTO.getPrice());
            existingSparePart.setStock(sparePartDTO.getStock());
            existingSparePart.setDescription(sparePartDTO.getDescription());
            existingSparePart.setCategory(categoryRepo.findById(sparePartDTO.getCategoryId()).orElse(null));

            SparePart updatedSparePart = sparePartRepo.save(existingSparePart);

            // Delete old images and add new ones
            sparePartImageRepo.findBySparePart_PartId(updatedSparePart.getPartId()).forEach(sparePartImageRepo::delete);
            updatedSparePart.setImages(saveSparePartImages(updatedSparePart, files));

            return modelMapper.map(updatedSparePart, SparePartDTO.class);
        }
        return null;
    }

    @Override
    public void deleteSparePart(int partId, String authorizationHeader) {
        // Delete related images first
        sparePartImageRepo.findBySparePart_PartId(partId).forEach(sparePartImageRepo::delete);
        // Then delete the spare part
        sparePartRepo.deleteById(partId);
    }

    private int extractSellerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }

    @Value("${jwt.secret}")
    private String secretKey;
}