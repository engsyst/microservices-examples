package ua.nure.it.microservice.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * A simplified DTO to represent a MenuItem from the restaurant service.
 * Used for fetching item details to calculate the total price of an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDtoForOrderService {
    private Long id;
    private String name;
    private BigDecimal price;
}
