package org.example.hondasupercub.service;

import com.itextpdf.text.DocumentException;
import org.example.hondasupercub.dto.TransactionDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface SellerTransactionService {

    List<TransactionDTO> getTransactionsBySeller(String authorizationHeader);

    TransactionDTO getTransactionDetails(int transactionId, String authorizationHeader);

    List<TransactionDTO> searchTransactionsBySellerAndOrderId(String authorizationHeader, String orderId);

    ByteArrayInputStream generateSellerTransactionPdfReport(String authToken)throws DocumentException, IOException;
}