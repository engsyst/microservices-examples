package ua.nure.it.microservice.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.it.microservice.paymentservice.entity.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentByOrderId(Long orderId);
}
