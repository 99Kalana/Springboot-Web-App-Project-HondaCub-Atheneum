package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.TransactionDTO;

import java.util.List;

public interface AdminTransactionService {
    List<TransactionDTO> getAllTransactions();
    TransactionDTO getTransactionById(int transactionId);
    List<TransactionDTO> getTransactionsByOrderId(int orderId);
    List<TransactionDTO> getTransactionsByPaymentStatus(String paymentStatus);
    List<TransactionDTO> getTransactionsByRefundStatus(String refundStatus);
}