package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.TransactionDTO;
import org.example.hondasupercub.service.impl.AdminTransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admintransactions")
public class AdminTransactionController {

    @Autowired
    private AdminTransactionServiceImpl transactionService;

    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<>(new ResponseDTO(200, "Transactions retrieved successfully", transactions), HttpStatus.OK);
    }

    @GetMapping("/get/{transactionId}")
    public ResponseEntity<ResponseDTO> getTransactionById(@PathVariable int transactionId) {
        TransactionDTO transaction = transactionService.getTransactionById(transactionId);
        if (transaction != null) {
            return new ResponseEntity<>(new ResponseDTO(200, "Transaction retrieved successfully", transaction), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDTO(404, "Transaction not found", null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/byOrder/{orderId}")
    public ResponseEntity<ResponseDTO> getTransactionsByOrderId(@PathVariable int orderId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByOrderId(orderId);
        return new ResponseEntity<>(new ResponseDTO(200, "Transactions retrieved by order ID", transactions), HttpStatus.OK);
    }

    @GetMapping("/byPaymentStatus/{paymentStatus}")
    public ResponseEntity<ResponseDTO> getTransactionsByPaymentStatus(@PathVariable String paymentStatus) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByPaymentStatus(paymentStatus);
        return new ResponseEntity<>(new ResponseDTO(200, "Transactions retrieved by payment status", transactions), HttpStatus.OK);
    }

    @GetMapping("/byRefundStatus/{refundStatus}")
    public ResponseEntity<ResponseDTO> getTransactionsByRefundStatus(@PathVariable String refundStatus) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByRefundStatus(refundStatus);
        return new ResponseEntity<>(new ResponseDTO(200, "Transactions retrieved by refund status", transactions), HttpStatus.OK);
    }
}