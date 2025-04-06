package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.TransactionDTO;
import org.example.hondasupercub.entity.*;
import org.example.hondasupercub.repo.*;
import org.example.hondasupercub.service.CustomerCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerCheckoutServiceImpl implements CustomerCheckoutService {

    @Autowired
    private CustomerCheckoutCartRepo cartRepo;

    @Autowired
    private CustomerCheckoutUserRepo userRepo;

    @Autowired
    private CustomerCheckoutSparePartRepo sparePartRepo;

    @Autowired
    private CustomerCheckoutOrderRepo orderRepo;

    @Autowired
    private CustomerCheckoutOrderDetailRepo orderDetailRepo;

    @Autowired
    private CustomerCheckoutTransactionRepo transactionRepo;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    @Transactional
    public void processCheckout(TransactionDTO transactionDTO) {
        System.out.println("Processing Checkout...");
        System.out.println("Authorization Header: " + transactionDTO.getAuthorizationHeader());

        String email = extractEmailFromToken(transactionDTO.getAuthorizationHeader());

        System.out.println("Extracted Email: " + email);

        if (email == null) {
            throw new RuntimeException("Invalid or missing authentication token");
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        System.out.println("User ID: " + user.getUserId());

        List<Cart> cartItems = cartRepo.findByUser(user);
        System.out.println("Cart Items Size before clear: " + cartItems.size());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setPlacedAt(transactionDTO.getTransactionDate());
        orderRepo.save(order);

        double totalAmount = 0;

        for (Cart cartItem : cartItems) {
            SparePart sparePart = cartItem.getSparePart();
            int quantity = cartItem.getQuantity();

            if (sparePart.getStock() < quantity) {
                throw new RuntimeException("Insufficient stock for part: " + sparePart.getPartName());
            }

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setSparePart(sparePart);
            orderDetail.setQuantity(quantity);
            orderDetail.setPrice(sparePart.getPrice());
            orderDetailRepo.save(orderDetail);

            sparePart.setStock(sparePart.getStock() - quantity);
            sparePartRepo.save(sparePart);

            totalAmount += sparePart.getPrice() * quantity;
        }

        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setUser(user);
        transaction.setPaymentMethod(Transaction.PaymentMethod.valueOf(transactionDTO.getPaymentMethod()));
        transaction.setPaymentStatus(Transaction.PaymentStatus.COMPLETED);
        transaction.setRefundStatus(Transaction.RefundStatus.NONE);
        transaction.setPaidAmount(transactionDTO.getPaidAmount());
        transaction.setTransactionDate(transactionDTO.getTransactionDate());
        transaction.setShippingAddress(transactionDTO.getShippingAddress());
        transaction.setContactNumber(transactionDTO.getContactNumber());
        transactionRepo.save(transaction);

        cartRepo.deleteByUser(user);
        System.out.println("Cart cleared.");
    }

    // Method to extract email from Authorization header
    private String extractEmailFromToken(String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                Claims claims = Jwts.parser().setSigningKey(secretKey)
                        .parseClaimsJws(authorizationHeader.substring(7)).getBody();
                return claims.getSubject();
            }
            return null; // Handle null or invalid format
        } catch (Exception e) {
            return null;
        }
    }
}