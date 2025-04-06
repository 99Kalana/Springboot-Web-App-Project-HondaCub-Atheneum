package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.TransactionDTO;
import org.example.hondasupercub.entity.Transaction;
import org.example.hondasupercub.repo.AdminTransactionRepo;
import org.example.hondasupercub.service.AdminTransactionService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminTransactionServiceImpl implements AdminTransactionService {

    @Autowired
    private AdminTransactionRepo transactionRepo;

    @Autowired
    private ModelMapper modelMapper;

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
}