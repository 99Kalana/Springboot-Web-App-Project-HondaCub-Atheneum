// CustomerCheckoutController.java
package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.TransactionDTO;
import org.example.hondasupercub.service.CustomerCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer/transactions")
public class CustomerCheckoutController {

    @Autowired
    private CustomerCheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<String> processCheckout(@RequestBody TransactionDTO transactionDTO) {
        try {
            checkoutService.processCheckout(transactionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction processed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}