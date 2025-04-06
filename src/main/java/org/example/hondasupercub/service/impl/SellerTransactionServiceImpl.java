package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.TransactionDTO;
import org.example.hondasupercub.entity.Transaction;
import org.example.hondasupercub.repo.SellerTransactionRepo;
import org.example.hondasupercub.service.SellerTransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SellerTransactionServiceImpl implements SellerTransactionService {

    @Autowired
    private SellerTransactionRepo transactionRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public List<TransactionDTO> getTransactionsBySeller(String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        List<Transaction> transactions = transactionRepo.findTransactionsBySellerId(sellerId);
        return transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionDetails(int transactionId, String authorizationHeader) {
        int sellerId = extractSellerIdFromToken(authorizationHeader);
        Optional<Transaction> transaction = transactionRepo.findTransactionByIdAndSellerId(transactionId, sellerId);
        return transaction.map(value -> modelMapper.map(value, TransactionDTO.class)).orElse(null);
    }

    private int extractSellerIdFromToken(String authorizationHeader) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authorizationHeader.substring(7)).getBody();
        return (Integer) claims.get("userId");
    }
}