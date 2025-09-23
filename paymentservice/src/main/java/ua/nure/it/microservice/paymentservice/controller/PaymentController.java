package ua.nure.it.microservice.paymentservice.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ua.nure.it.microservice.paymentservice.dto.CreatePaymentRequestDto;
import ua.nure.it.microservice.paymentservice.dto.PaymentDto;
import ua.nure.it.microservice.paymentservice.service.AlreadyExistException;
import ua.nure.it.microservice.paymentservice.service.PaymentService;

/**
 * REST controller for the Payment Service.
 * Handles incoming payment requests and delegates to the PaymentService.
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping(path = "/order/{orderId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public Mono<ResponseEntity<PaymentDto>> getPaymentByOrderId(@PathVariable Long orderId) {
        return Mono.fromCallable(
                () -> ResponseEntity.ok()
                        .body(PaymentDto.of(paymentService.getPaymentByOrderId(orderId)
                                .orElseThrow(() -> new EntityNotFoundException("Payment for order ID "
                                + orderId + "not found")))))
                .onErrorMap(throwable -> throwable instanceof EntityNotFoundException,
                        throwable -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                throwable.getMessage()))
                .log();
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public Mono<ResponseEntity<PaymentDto>> createPayment(@Valid @RequestBody CreatePaymentRequestDto requestDto) {
        return Mono.fromCallable(() -> ResponseEntity.ok()
                        .body(PaymentDto.of(paymentService.processPayment(requestDto))))
                .onErrorMap(throwable -> throwable instanceof AlreadyExistException,
                        throwable -> new ResponseStatusException(HttpStatus.ALREADY_REPORTED,
                                "Order ID " + requestDto.orderId() + " already paid."));
    }
}