package org.example.hondasupercub.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.hondasupercub.dto.TransactionDTO;
import org.example.hondasupercub.entity.Transaction;
import org.example.hondasupercub.repo.AdminTransactionRepo;
import org.example.hondasupercub.service.AdminTransactionService;
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
public class AdminTransactionServiceImpl implements AdminTransactionService {

    @Autowired
    private AdminTransactionRepo transactionRepo;

    @Autowired
    private ModelMapper modelMapper;

    private static final Font MAIN_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
    private static final Font SUB_TOPIC_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final BaseColor TABLE_HEADER_BG_COLOR = BaseColor.LIGHT_GRAY;
    private static final Font DATE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);


    @Override
    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepo.findAll();
        return modelMapper.map(transactions, new TypeToken<List<TransactionDTO>>() {}.getType());
    }

    @Override
    public TransactionDTO getTransactionById(int transactionId) {
        Optional<Transaction> transaction = transactionRepo.findById(transactionId);
        return transaction.map(value -> modelMapper.map(value, TransactionDTO.class)).orElse(null);
    }

    @Override
    public List<TransactionDTO> getTransactionsByOrderId(int orderId) {
        List<Transaction> transactions = transactionRepo.findByOrder_OrderId(orderId);
        return modelMapper.map(transactions, new TypeToken<List<TransactionDTO>>() {}.getType());
    }

    @Override
    public List<TransactionDTO> getTransactionsByPaymentStatus(String paymentStatus) {
        Transaction.PaymentStatus status = Transaction.PaymentStatus.valueOf(paymentStatus.toUpperCase());
        List<Transaction> transactions = transactionRepo.findByPaymentStatus(status);
        return modelMapper.map(transactions, new TypeToken<List<TransactionDTO>>() {}.getType());
    }

    @Override
    public List<TransactionDTO> getTransactionsByRefundStatus(String refundStatus) {
        Transaction.RefundStatus status = Transaction.RefundStatus.valueOf(refundStatus.toUpperCase());
        List<Transaction> transactions = transactionRepo.findByRefundStatus(status);
        return modelMapper.map(transactions, new TypeToken<List<TransactionDTO>>() {}.getType());
    }

    public ByteArrayInputStream generateTransactionPdfReport() throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Main Topic
        Paragraph mainTopic = new Paragraph("Honda Cub Atheneum", MAIN_TOPIC_FONT);
        mainTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(mainTopic);

        // Sub Topic
        Paragraph subTopic = new Paragraph("Admin Transaction Report", SUB_TOPIC_FONT);
        subTopic.setAlignment(Element.ALIGN_CENTER);
        document.add(subTopic);

        document.add(Chunk.NEWLINE);

        // Date and Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Paragraph dateTime = new Paragraph("Generated on: " + dtf.format(LocalDateTime.now()), DATE_FONT);
        dateTime.setAlignment(Element.ALIGN_LEFT);
        document.add(dateTime);

        document.add(Chunk.NEWLINE);

        // Fetch all transactions
        List<Transaction> transactions = transactionRepo.findAll();

        // Create PDF table
        PdfPTable table = new PdfPTable(8); // Adjust column count
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add table headers
        Stream.of("Transaction ID", "Order ID", "User ID", "Payment Method", "Payment Status", "Refund Status", "Paid Amount", "Transaction Date")
                .forEach(columnTitle -> {
                    PdfPCell headerCell = new PdfPCell();
                    headerCell.setBackgroundColor(TABLE_HEADER_BG_COLOR);
                    headerCell.setBorderWidth(1);
                    headerCell.setPhrase(new Phrase(columnTitle, TABLE_HEADER_FONT));
                    table.addCell(headerCell);
                });

        // Add table data
        for (Transaction transaction : transactions) {
            table.addCell(new Phrase(String.valueOf(transaction.getTransactionId()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(transaction.getOrder().getOrderId()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(transaction.getUser().getUserId()), CELL_FONT));
            table.addCell(new Phrase(String.valueOf(transaction.getPaymentMethod()), CELL_FONT));
            table.addCell(new Phrase(transaction.getPaymentStatus().toString(), CELL_FONT));
            table.addCell(new Phrase(transaction.getRefundStatus().toString(), CELL_FONT));
            table.addCell(new Phrase("$" + transaction.getPaidAmount(), CELL_FONT));
            table.addCell(new Phrase(transaction.getTransactionDate(), CELL_FONT));
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("End of Transaction Report", FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20f);
        document.add(footer);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}