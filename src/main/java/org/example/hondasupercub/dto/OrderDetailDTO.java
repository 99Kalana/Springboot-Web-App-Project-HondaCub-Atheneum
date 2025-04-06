package org.example.hondasupercub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetailDTO {

    private int orderDetailId;

    private int orderId;  // Store only the orderId, not the full Order entity

    private int sparePartId;  // Store only the sparePartId, not the full SparePart entity

    private int quantity;

    private double price;

    private String sparePartName; // new addition to receive order history by spare part name

}
