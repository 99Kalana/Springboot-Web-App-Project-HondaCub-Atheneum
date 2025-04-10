package org.example.hondasupercub.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.ReviewDTO;
import org.example.hondasupercub.entity.Review;
import org.example.hondasupercub.repo.SellerReviewRepo;
import org.example.hondasupercub.service.SellerReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SellerReviewServiceImpl implements SellerReviewService {

    @Autowired
    private SellerReviewRepo reviewRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final Font MAIN_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
    private static final Font SUB_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final BaseColor TABLE_HEADER_BG_COLOR = BaseColor.LIGHT_GRAY;
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);


    private int extractSellerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }

    @Override
    public List<ReviewDTO> getReviewsBySeller(String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        List<Review> reviews = reviewRepo.findReviewsBySellerId(sellerId);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> getReviewsBySellerAndPartId(String authorizationHeader, Integer partId) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        List<Review> reviews = reviewRepo.findReviewsBySellerIdAndSparePart_PartId(sellerId, partId);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO replyToReview(int reviewId, String replyComment, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Review review = reviewRepo.findByReviewId(reviewId);

        if (review != null && review.getSparePart().getSeller().getUserId() == sellerId) {
            review.setComment(review.getComment() + "\nSeller Reply: " + replyComment);
            Review updatedReview = reviewRepo.save(review);
            return modelMapper.map(updatedReview, ReviewDTO.class);
        }
        return null;
    }

    public ByteArrayInputStream generateSellerReviewPdfReport(String authorizationHeader) throws DocumentException, IOException {
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
        Paragraph subTopic = new Paragraph("Seller Reviews & Ratings (Seller ID: " + sellerId + ")", SUB_TOPIC_FONT);
        subTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(subTopic);

        document.add(Chunk.NEWLINE);

        // Date and Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateTime = new Paragraph("Generated on: " + dtf.format(LocalDateTime.now()), DATE_FONT);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        document.add(dateTime);

        document.add(Chunk.NEWLINE);

        // Fetch seller's reviews
        List<Review> reviews = reviewRepo.findReviewsBySellerId(sellerId);

        // Create PDF table
        PdfPTable table = new PdfPTable(6); // Adjust column count based on displayed data
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Stream.of("Review ID", "User ID", "Part ID", "Rating", "Comment", "Review Date")
                .forEach(columnTitle -> {
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(TABLE_HEADER_BG_COLOR);
                    headerCell.setBorderWidth(1);
                    headerCell.setPhrase(new Phrase(columnTitle, TABLE_HEADER_FONT));
                    table.addCell(headerCell);
                });

        // Add table data
        for (Review review : reviews) {
            table.addCell(new Phrase(String.valueOf(review.getReviewId()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(review.getUser().getUserId()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(review.getSparePart().getPartId()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(review.getRating()), CELL_FONT));
            table.addCell(new Phrase(review.getComment(), CELL_FONT));
            table.addCell(new Phrase(review.getReviewDate().toString(), CELL_FONT));
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("End of Seller Reviews Report", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

}