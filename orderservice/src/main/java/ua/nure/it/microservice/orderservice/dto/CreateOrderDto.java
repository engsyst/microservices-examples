package ua.nure.it.microservice.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nure.it.microservice.orderservice.entity.Order;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDto {
    @NotNull(message = "Menu item IDs cannot be null")
    @Size(min = 1, message = "Menu item IDs cannot be empty.")
    private List<Long> menuItemIds;

    @NotBlank(message = "Delivery address cannot be blank")
    private String deliveryAddress;

    public Order toEntity() {
        // Note: total price and order status will be set by the service layer
        return Order.builder()
                .menuItemIds(this.menuItemIds)
                .deliveryAddress(this.deliveryAddress)
                .build();
    }
}
