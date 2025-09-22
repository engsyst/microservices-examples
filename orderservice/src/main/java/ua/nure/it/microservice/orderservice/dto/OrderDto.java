package ua.nure.it.microservice.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nure.it.microservice.orderservice.entity.Order;
import ua.nure.it.microservice.orderservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private List<Long> menuItemIds;
    private BigDecimal totalPrice;
    private String deliveryAddress;
    private OrderStatus orderStatus;
    private Long paymentId;

    public static OrderDto of(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .menuItemIds(order.getMenuItemIds())
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .orderStatus(order.getOrderStatus())
                .paymentId(order.getPaymentId())
                .build();
    }

    public static Order toEntity(OrderDto dto) {
        return new Order(
                dto.getId(),
                dto.getMenuItemIds(),
                dto.getTotalPrice(),
                dto.getDeliveryAddress(),
                dto.getOrderStatus(),
                dto.getPaymentId()
        );
    }
}
