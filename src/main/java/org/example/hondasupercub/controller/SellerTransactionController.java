package org.example.hondasupercub.controller;

import com.itextpdf.text.DocumentException;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.TransactionDTO;
import org.example.hondasupercub.service.SellerTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/seller/transactions")
public class SellerTransactionController {

    @Autowired
    private SellerTransactionService sellerTransactionService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getTransactions(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "orderId", required = false) String orderId) {
        List<TransactionDTO> transactions;
        if (orderId != null && !orderId.trim().isEmpty()) {
            transactions = sellerTransactionService.searchTransactionsBySellerAndOrderId(authorizationHeader, orderId.trim());
        } else {
            transactions = sellerTransactionService.getTransactionsBySeller(authorizationHeader);
        }
        return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Transactions fetched successfully", transactions), HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ResponseDTO> getTransactionDetails(
            @PathVariable int transactionId,
            @RequestHeader("Authorization") String authorizationHeader) {

        TransactionDTO transaction = sellerTransactionService.getTransactionDetails(transactionId, authorizationHeader);
        if (transaction != null) {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.OK.value(), "Transaction details fetched successfully", transaction), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(HttpStatus.NOT_FOUND.value(), "Transaction not found or not belonging to seller", null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/report/download")
    public ResponseEntity<InputStreamResource> downloadSellerTransactionReport(
            @RequestHeader("Authorization") String authorizationHeader) throws IOException, DocumentException {
        ByteArrayInputStream pdfReport = sellerTransactionService.generateSellerTransactionPdfReport(authorizationHeader);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=seller_transaction_report.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(pdfReport));
    }
}