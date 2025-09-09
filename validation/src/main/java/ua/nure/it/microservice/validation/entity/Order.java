package ua.nure.it.microservice.validation.entity;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Order {
    private Long id;
    private List<Long> menuItemIds; // IDs of menu items
    private BigDecimal totalPrice;
    private String deliveryAddress;
    private OrderStatus orderStatus;
    private Long paymentId;
}