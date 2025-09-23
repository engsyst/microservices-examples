package ua.nure.it.microservice.paymentservice.dto;

import ua.nure.it.microservice.paymentservice.entity.PaymentStatus;

public record UpdateOrderStatusRequestDto(Long id, String orderStatus) {
}
