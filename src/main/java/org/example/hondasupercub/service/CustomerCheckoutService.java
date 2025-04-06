package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.TransactionDTO;

public interface CustomerCheckoutService {
    void processCheckout(TransactionDTO transactionDTO);
}
