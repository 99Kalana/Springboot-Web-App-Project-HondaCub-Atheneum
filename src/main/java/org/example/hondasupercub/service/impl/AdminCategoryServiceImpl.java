package org.example.hondasupercub.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.hondasupercub.dto.CategoryDTO;
import org.example.hondasupercub.entity.Category;
import org.example.hondasupercub.repo.AdminCategoryRepo;
import org.example.hondasupercub.service.AdminCategoryService;
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
public class AdminCategoryServiceImpl implements AdminCategoryService {
    @Autowired
    private AdminCategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    // Define fonts
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE);
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA);

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        categoryRepo.save(modelMapper.map(categoryDTO, Category.class));
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        return modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {}.getType());
    }

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        if (!categoryRepo.existsById(categoryDTO.getCategoryId())) {
            throw new RuntimeException("Category does not exist");
        }
        categoryRepo.save(modelMapper.map(categoryDTO, Category.class));
    }

    @Override
    public void deleteCategory(int id) {
        categoryRepo.deleteById(id);
    }

    @Override
    public CategoryDTO getCategoryById(int id) {
        Optional<Category> category = categoryRepo.findById(id);
        return category.map(value -> modelMapper.map(value, CategoryDTO.class)).orElse(null);
    }

    @Override
    public List<CategoryDTO> searchCategories(String term) {
        List<Category> categories = categoryRepo.findByCategoryNameContaining(term);
        return modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {}.getType());
    }

    public ByteArrayInputStream generateCategoryPdfReport() throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Header
        Paragraph header = new Paragraph("Honda Cub Atheneum", HEADER_FONT);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        // Sub Topic
        Font subTopicFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph subTopic = new Paragraph("Admin Category Management Report", subTopicFont);
        subTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(subTopic);

        document.add(Chunk.NEWLINE);

        // Date and Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateTime = new Paragraph("Generated on: " + dtf.format(LocalDateTime.now()), DATE_FONT);
        dateTime.setAlignment(Element.ALIGN_LEFT); // Corrected alignment
        document.add(dateTime);

        document.add(Chunk.NEWLINE);

        // Fetch category data
        List<Category> categories = categoryRepo.findAll();

        // Create PDF table
        PdfPTable table = new PdfPTable(3); // 3 columns: ID, Name, Description
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Stream.of("ID", "Category Name", "Description")
                .forEach(columnTitle -> {
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    headerCell.setBorderWidth(1);
                    headerCell.setPhrase(new Phrase(columnTitle, TABLE_HEADER_FONT));
                    table.addCell(headerCell);
                });

        // Add table data
        for (Category category : categories) {
            table.addCell(new Phrase(String.valueOf(category.getCategoryId()), CELL_FONT));
            table.addCell(new Phrase(category.getCategoryName(), CELL_FONT));
            table.addCell(new Phrase(category.getDescription(), CELL_FONT));
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("End of Category Report", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}