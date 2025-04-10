package org.example.hondasupercub.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.hondasupercub.dto.UserDTO;
import org.example.hondasupercub.entity.User;
import org.example.hondasupercub.repo.AdminUserRepo;
import org.example.hondasupercub.service.AdminUserService;
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
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private AdminUserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    // Define fonts and colors for styling
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE);
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA);
    private static final BaseColor TABLE_HEADER_BG_COLOR = BaseColor.LIGHT_GRAY; // Added this line



    @Override
    public void addUser(UserDTO userDTO) {
        if (userRepo.existsById(userDTO.getUserId())) {
            throw new RuntimeException("User already exists");
        }
        userRepo.save(modelMapper.map(userDTO, User.class));
    }


    @Override
    public List<UserDTO> getAllUsers(String role) {
        List<User> users;
        if (role != null && !role.equals("all")) {
            users = userRepo.findByRole(User.UserRole.valueOf(role));
        } else {
            users = userRepo.findAll();
        }
        return modelMapper.map(users, new TypeToken<List<UserDTO>>() {}.getType());
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        if (!userRepo.existsById(userDTO.getUserId())) {
            throw new RuntimeException("User does not exist");
        }
        userRepo.save(modelMapper.map(userDTO, User.class));
    }

    @Override
    public void deleteUser(int id) {
        userRepo.deleteById(id);
    }

    @Override
    public UserDTO getUserById(int id) {
        Optional<User> user = userRepo.findById(id);
        return user.map(value -> modelMapper.map(value, UserDTO.class)).orElse(null);
    }

    @Override
    public void updateUserStatus(int id, String status) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(User.UserStatus.valueOf(status));
            userRepo.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public List<UserDTO> searchUsers(String term) {
        List<User> users = userRepo.findByFullNameContainingOrEmailContaining(term, term);
        return modelMapper.map(users, new TypeToken<List<UserDTO>>() {}.getType());
    }

    public ByteArrayInputStream generateUserPdfReport() throws DocumentException, IOException {
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
        Paragraph subTopic = new Paragraph("Admin User Management Report", subTopicFont);
        subTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(subTopic);

        document.add(Chunk.NEWLINE);

        // Date and Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateTime = new Paragraph("Generated on: " + dtf.format(LocalDateTime.now()), DATE_FONT);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        document.add(dateTime);

        document.add(Chunk.NEWLINE);

        // Fetch user data
        List<User> users = userRepo.findAll();

        // Create PDF table
        PdfPTable table = new PdfPTable(5); // Adjust the number of columns based on User entity fields
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Stream.of("ID", "Full Name", "Email", "Role", "Status") // Adjust column names
                .forEach(columnTitle -> {
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(TABLE_HEADER_BG_COLOR);
                    headerCell.setBorderWidth(1);
                    headerCell.setPhrase(new Phrase(columnTitle, TABLE_HEADER_FONT));
                    table.addCell(headerCell);
                });

        // Add table data
        for (User user : users) {
            table.addCell(new Phrase(String.valueOf(user.getUserId()), CELL_FONT));
            table.addCell(new Phrase(user.getFullName(), CELL_FONT));
            table.addCell(new Phrase(user.getEmail(), CELL_FONT));
            table.addCell(new Phrase(user.getRole().toString(), CELL_FONT));
            table.addCell(new Phrase(user.getStatus().toString(), CELL_FONT));
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("End of User Report", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}
