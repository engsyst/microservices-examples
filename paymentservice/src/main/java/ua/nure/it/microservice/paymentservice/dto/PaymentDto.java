package ua.nure.it.microservice.paymentservice.dto;

import ua.nure.it.microservice.paymentservice.entity.Payment;
import ua.nure.it.microservice.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for transferring payment information to clients.
 */
public record PaymentDto(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus paymentStatus,
        LocalDateTime paymentDate) {

    public static PaymentDto of(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getPaymentDate()
        );
    }
}