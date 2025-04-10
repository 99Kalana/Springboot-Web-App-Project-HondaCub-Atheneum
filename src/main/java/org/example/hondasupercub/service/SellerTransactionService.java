package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.TransactionDTO;

import java.util.List;

public interface SellerTransactionService {

    List<TransactionDTO> getTransactionsBySeller(String authorizationHeader);

    TransactionDTO getTransactionDetails(int transactionId, String authorizationHeader);

    List<TransactionDTO> searchTransactionsBySellerAndOrderId(String authorizationHeader, String orderId);
}