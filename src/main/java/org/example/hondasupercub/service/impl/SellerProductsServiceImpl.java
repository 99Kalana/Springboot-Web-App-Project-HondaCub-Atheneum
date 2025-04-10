package org.example.hondasupercub.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final Font MAIN_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
    private static final Font SUB_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final BaseColor TABLE_HEADER_BG_COLOR = BaseColor.LIGHT_GRAY;
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);


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

    public ByteArrayInputStream generateSellerProductsPdfReport(String authorizationHeader) throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Extract Seller ID from Token
        int sellerId = extractSellerIdFromToken(authorizationHeader);

        // Main Topic
        Paragraph mainTopic = new Paragraph("Honda Cub Atheneum - Seller Report", MAIN_TOPIC_FONT);
        mainTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(mainTopic);

        // Sub Topic
        Paragraph subTopic = new Paragraph("Seller Product Management (Seller ID: " + sellerId + ")", SUB_TOPIC_FONT);
        subTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(subTopic);

        document.add(Chunk.NEWLINE);

        // Date and Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateTime = new Paragraph("Generated on: " + dtf.format(LocalDateTime.now()), DATE_FONT);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        document.add(dateTime);

        document.add(Chunk.NEWLINE);

        // Fetch seller's products
        List<SparePart> products = sparePartRepo.findBySeller_UserId(sellerId);

        // Create PDF table
        PdfPTable table = new PdfPTable(5); // Part ID, Name, Category, Price, Stock
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Stream.of("Part ID", "Part Name", "Category", "Price", "Stock")
                .forEach(columnTitle -> {
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(TABLE_HEADER_BG_COLOR);
                    headerCell.setBorderWidth(1);
                    headerCell.setPhrase(new Phrase(columnTitle, TABLE_HEADER_FONT));
                    table.addCell(headerCell);
                });

        // Add table data
        for (SparePart product : products) {
            table.addCell(new Phrase(String.valueOf(product.getPartId()), CELL_FONT));
            table.addCell(new Phrase(product.getPartName(), CELL_FONT));
            table.addCell(new Phrase(product.getCategory().getCategoryName(), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(product.getPrice()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(product.getStock()), CELL_FONT));
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("End of Seller Products Report", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}