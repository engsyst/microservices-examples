package ua.nure.it.microservice.orderservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PaymentDto {
    Long id;
    Long orderId;
    BigDecimal amount;
    String paymentStatus;
    LocalDateTime paymentDate;
}
