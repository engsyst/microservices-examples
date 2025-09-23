package ua.nure.it.microservice.orderservice.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ua.nure.it.microservice.orderservice.entity.Order;

@Getter
@Setter
@Builder
public class PayOrderRequestDto {
    private Long orderId;
    private BigDecimal amount;

    public static PayOrderRequestDto of(Order order) {
        return builder()
                .orderId(order.getId())
                .amount(order.getTotalPrice())
                .build();
    }
}