package ua.nure.it.microservice.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ua.nure.it.microservice.paymentservice.entity.Payment;
import ua.nure.it.microservice.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for a new payment request from a client.
 */
@Builder
public record CreatePaymentRequestDto(
        @NotNull(message = "Order ID cannot be null")
        Long orderId,
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be a positive number")
        BigDecimal amount) {

    public Payment toEntity() {
        return Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING) // Default status for new payments
                .paymentDate(LocalDateTime.now())
                .build();
    }
}
