package org.example.hondasupercub.service.impl;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.entity.SparePart; // Assuming you have a SparePart entity
import org.example.hondasupercub.repo.AdminSparePartRepo;
import org.example.hondasupercub.service.AdminSparePartService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class AdminSparePartServiceImpl implements AdminSparePartService {

    @Autowired
    private AdminSparePartRepo sparePartRepo;



    @Autowired
    private ModelMapper modelMapper;

    // Define fonts
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE);
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA);
    private static final BaseColor TABLE_HEADER_BG_COLOR = BaseColor.LIGHT_GRAY;

    @Override
    public List<SparePartDTO> getAllSparePartsDTO() {
        List<SparePart> spareParts = sparePartRepo.findAll();
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }

    @Override
    public SparePartDTO getSparePartDTOById(Integer id) {
        Optional<SparePart> sparePart = sparePartRepo.findById(id);
        return sparePart.map(value -> modelMapper.map(value, SparePartDTO.class)).orElse(null);
    }


    @Override
    public List<SparePartDTO> searchSparePartsDTO(String query) {
        List<SparePart> spareParts = sparePartRepo.findByPartNameContainingIgnoreCase(query);
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }

    @Override
    public List<SparePartDTO> filterSparePartsByCategoryDTO(String categoryName) {
        List<SparePart> spareParts = sparePartRepo.findByCategory_CategoryNameIgnoreCase(categoryName);
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }

    public ByteArrayInputStream generateSparePartPdfReport() throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Main Topic
        Font mainTopicFont = FontFactory.getFont(String.valueOf(HEADER_FONT.getBaseFont()), HEADER_FONT.getSize() + 4, HEADER_FONT.getStyle(), HEADER_FONT.getColor());
        Paragraph mainTopic = new Paragraph("Honda Cub Atheneum", mainTopicFont);
        mainTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(mainTopic);

        // Sub Topic
        Font subTopicFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph subTopic = new Paragraph("Admin Sparepart Management Report", subTopicFont);
        subTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(subTopic);

        document.add(Chunk.NEWLINE);

        // Date and Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateTime = new Paragraph("Generated on: " + dtf.format(LocalDateTime.now()), DATE_FONT);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        document.add(dateTime);

        document.add(Chunk.NEWLINE);

        // Fetch spare part data
        List<SparePart> spareParts = sparePartRepo.findAll();

        // Create PDF table
        PdfPTable table = new PdfPTable(7); // Adjust the number of columns based on SparePart entity
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Stream.of("Part ID", "Part Name", "Description", "Price", "Stock", "Category", "Seller ID") // Adjust column names
                .forEach(columnTitle -> {
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(TABLE_HEADER_BG_COLOR);
                    headerCell.setBorderWidth(1);
                    headerCell.setPhrase(new Phrase(columnTitle, TABLE_HEADER_FONT));
                    table.addCell(headerCell);
                });

        // Add table data
        for (SparePart sparePart : spareParts) {
            table.addCell(new Phrase(String.valueOf(sparePart.getPartId()), CELL_FONT));
            table.addCell(new Phrase(sparePart.getPartName(), CELL_FONT));
            table.addCell(new Phrase(sparePart.getDescription(), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(sparePart.getPrice()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(sparePart.getStock()), CELL_FONT));
            table.addCell(new Phrase(sparePart.getCategory().getCategoryName(), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(sparePart.getSeller().getUserId()), CELL_FONT));
            // Note: Images are not directly added to this text-based report
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("End of Spare Parts Report", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}