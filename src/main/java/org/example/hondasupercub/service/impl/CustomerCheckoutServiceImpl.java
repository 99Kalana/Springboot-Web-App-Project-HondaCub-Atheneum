package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.mail.internet.MimeMessage;
import org.example.hondasupercub.dto.TransactionDTO;
import org.example.hondasupercub.entity.*;
import org.example.hondasupercub.repo.*;
import org.example.hondasupercub.service.CustomerCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
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

    @Autowired
    private CustomerRewardRepo rewardRepo; // Need Reward Repo

    @Autowired
    private JavaMailSender javaMailSender;

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

        Reward customerReward = rewardRepo.findByUser_UserId(user.getUserId()).orElse(new Reward());
        int availablePoints = customerReward.getPoints();
        int redeemedPoints = transactionDTO.getRedeemedPoints();

        if (redeemedPoints > 0) {
            if (redeemedPoints < 50) {
                throw new RuntimeException("Minimum points to redeem is 50.");
            }
            if (redeemedPoints > availablePoints) {
                throw new RuntimeException("Cannot redeem more points than available.");
            }
        }

        List<Cart> cartItems = cartRepo.findByUser(user);
        System.out.println("Cart Items Size before clear: " + cartItems.size());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setPlacedAt(transactionDTO.getTransactionDate());
        orderRepo.save(order);

        double totalAmountBeforeDiscount = 0;
        StringBuilder orderDetailsTable = new StringBuilder();

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

            double itemTotal = sparePart.getPrice() * quantity;
            totalAmountBeforeDiscount += itemTotal;
            orderDetailsTable.append("<tr>")
                    .append("<td style=\"padding: 8px; border-bottom: 1px solid #ddd;\">").append(sparePart.getPartName()).append("</td>")
                    .append("<td style=\"padding: 8px; border-bottom: 1px solid #ddd;\">$").append(String.format("%.2f", sparePart.getPrice())).append("</td>")
                    .append("<td style=\"padding: 8px; border-bottom: 1px solid #ddd;\">").append(quantity).append("</td>")
                    .append("<td style=\"padding: 8px; border-bottom: 1px solid #ddd;\">$").append(String.format("%.2f", itemTotal)).append("</td>")
                    .append("</tr>");
        }

        double discountPercentage = (redeemedPoints / 50.0) * 0.02; // Calculate discount
        double discountAmount = totalAmountBeforeDiscount * discountPercentage;
        double finalAmount = totalAmountBeforeDiscount - discountAmount;

        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setUser(user);
        transaction.setPaymentMethod(Transaction.PaymentMethod.valueOf(transactionDTO.getPaymentMethod()));
        transaction.setPaymentStatus(Transaction.PaymentStatus.COMPLETED);
        transaction.setRefundStatus(Transaction.RefundStatus.NONE);
        transaction.setPaidAmount(finalAmount);
        transaction.setTransactionDate(transactionDTO.getTransactionDate());
        transaction.setShippingAddress(transactionDTO.getShippingAddress());
        transaction.setContactNumber(transactionDTO.getContactNumber());
        transaction.setRedeemedPoints(redeemedPoints); // Save redeemed points
        transactionRepo.save(transaction);

        if (redeemedPoints > 0) {
            customerReward.setPoints(availablePoints - redeemedPoints);
            customerReward.setRedeemedPoints(customerReward.getRedeemedPoints() + redeemedPoints);
            rewardRepo.save(customerReward);
        }

        cartRepo.deleteByUser(user);
        System.out.println("Cart cleared.");

        // Send styled email with discount info
        sendStyledOrderConfirmationEmail(user.getEmail(), orderDetailsTable.toString(), totalAmountBeforeDiscount, discountAmount, finalAmount, transactionDTO.getShippingAddress(), transactionDTO.getContactNumber());
    }

    // Method to extract email from Authorization header (remains the same)
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

    private void sendStyledOrderConfirmationEmail(String toEmail, String orderDetailsTableRows, double totalAmountBeforeDiscount, double discountAmount, double finalAmount, String shippingAddress, String contactNumber) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("HondaCub Atheneum - Order Confirmation");

            String emailContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;\">\n" +
                    "    <h2 style=\"color: #007bff; text-align: center; margin-bottom: 20px;\">Thank you for your order!</h2>\n" +
                    "    <p style=\"margin-bottom: 10px;\">Dear Customer,</p>\n" +
                    "    <p style=\"margin-bottom: 10px;\">Your order has been placed successfully. Here are the details:</p>\n" +
                    "    <h3 style=\"color: #343a40; margin-bottom: 10px;\">Order Details:</h3>\n" +
                    "    <table style=\"width: 100%; border-collapse: collapse; margin-bottom: 15px;\">\n" +
                    "        <thead>\n" +
                    "            <tr>\n" +
                    "                <th style=\"padding: 8px; border-bottom: 2px solid #343a40; text-align: left;\">Part Name</th>\n" +
                    "                <th style=\"padding: 8px; border-bottom: 2px solid #343a40; text-align: left;\">Unit Price</th>\n" +
                    "                <th style=\"padding: 8px; border-bottom: 2px solid #343a40; text-align: left;\">Quantity</th>\n" +
                    "                <th style=\"padding: 8px; border-bottom: 2px solid #343a40; text-align: left;\">Total Price</th>\n" +
                    "            </tr>\n" +
                    "        </thead>\n" +
                    "        <tbody>\n" +
                    orderDetailsTableRows +
                    "        </tbody>\n" +
                    "        <tfoot>\n" +
                    "            <tr>\n" +
                    "                <td colspan=\"3\" style=\"padding: 8px; font-weight: bold; text-align: right;\">Subtotal:</td>\n" +
                    "                <td style=\"padding: 8px; text-align: right;\">$" + String.format("%.2f", totalAmountBeforeDiscount) + "</td>\n" +
                    "            </tr>\n" +
                    "            <tr>\n" +
                    "                <td colspan=\"3\" style=\"padding: 8px; font-weight: bold; text-align: right;\">Discount Applied:</td>\n" +
                    "                <td style=\"padding: 8px; text-align: right; color: green;\">-$" + String.format("%.2f", discountAmount) + "</td>\n" +
                    "            </tr>\n" +
                    "            <tr>\n" +
                    "                <td colspan=\"3\" style=\"padding: 8px; font-weight: bold; text-align: right;\">Total Amount:</td>\n" +
                    "                <td style=\"padding: 8px; font-weight: bold; color: #28a745; text-align: right;\">$" + String.format("%.2f", finalAmount) + "</td>\n" +
                    "            </tr>\n" +
                    "        </tfoot>\n" +
                    "    </table>\n" +
                    "    <h3 style=\"color: #343a40; margin-bottom: 10px;\">Shipping Information:</h3>\n" +
                    "    <p style=\"margin-left: 20px; margin-bottom: 10px;\"><strong>Shipping Address:</strong> " + shippingAddress + "</p>\n" +
                    "    <p style=\"margin-left: 20px; margin-bottom: 15px;\"><strong>Contact Number:</strong> " + contactNumber + "</p>\n" +
                    "    <p style=\"margin-bottom: 10px;\">We are currently processing your order and will notify you via order history when it ships.</p>\n" +
                    "    <p style=\"margin-top: 20px;\">Sincerely,<br>The HondaCub Atheneum Team</p>\n" +
                    "</div>";

            helper.setText(emailContent, true); // Set the content as HTML
            javaMailSender.send(message);

        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}