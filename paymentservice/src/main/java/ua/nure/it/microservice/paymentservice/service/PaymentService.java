package ua.nure.it.microservice.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ua.nure.it.microservice.paymentservice.dto.CreatePaymentRequestDto;
import ua.nure.it.microservice.paymentservice.entity.Payment;
import ua.nure.it.microservice.paymentservice.entity.PaymentStatus;
import ua.nure.it.microservice.paymentservice.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * Service to handle payment processing.
 * It simulates a payment gateway and communicates with the Order Service.
 */
@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    static private final Random r = new Random();

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Processes a payment request by simulating a payment
     *
     * @param requestDto The DTO containing payment details.
     * @return A Mono containing the created Payment entity.
     */
    @Transactional
    public Payment processPayment(CreatePaymentRequestDto requestDto) {
        log.info("Processing payment for order ID: {}", requestDto.orderId());

        Optional<Payment> paymentOptional = paymentRepository.findPaymentByOrderId(requestDto.orderId());
        if (paymentOptional.isPresent()) {
            log.error("Payment ID {} for order ID {} already exists",
                    paymentOptional.get().getId(), paymentOptional.get().getOrderId());
            throw new AlreadyExistException();
        }
        PaymentStatus paymentStatus = simulatePayment(requestDto.amount());
        Payment payment = new Payment(null, requestDto.orderId(), requestDto.amount(),
                paymentStatus, LocalDateTime.now());

        payment.setPaymentStatus(paymentStatus);
        log.debug("Payment {}", payment);
        if (paymentStatus.equals(PaymentStatus.SUCCESS)) {
            payment = paymentRepository.save(payment);
        }
        return payment;
    }

    /**
     * Finds a payment by its associated order ID.
     *
     * @param orderId The ID of the order.
     * @return A Mono containing the Payment entity, or a Mono with an error if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findPaymentByOrderId(orderId);
    }

    /**
     * Simulates a call to an external payment gateway.
     *
     * @param amount The amount to process.
     * @return The resulting payment status.
     */
    private PaymentStatus simulatePayment(BigDecimal amount) {
        // A simple stub: payments over simulated amount on the customer account fail.
        return amount.compareTo(BigDecimal.valueOf(r.nextFloat(300))) <= 0
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;
    }
}
