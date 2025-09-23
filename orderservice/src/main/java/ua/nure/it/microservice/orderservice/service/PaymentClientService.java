package ua.nure.it.microservice.orderservice.service;

//import jakarta.ws.rs.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import ua.nure.it.microservice.orderservice.dto.PayOrderRequestDto;
import ua.nure.it.microservice.orderservice.dto.PaymentDto;

import java.net.ConnectException;
import java.time.Duration;
import java.util.function.Function;

/**
 * Service to communicate with the Payment Service.
 * It handles payment requests and implements a retry mechanism for transient failures.
 */
@Service
@Slf4j
public class PaymentClientService {

    @Value("${order.paymentclient.maxAttempts:3}")
    int maxAttempts;
    @Value("${order.paymentclient.retryTimeout:2}")
    int retryTimeout;
    @Value("${order.paymentclient.jitterFactor:0.5}")
    double jitterFactor;
    @Value("${order.paymentclient.maxBackoffTimeout:10}")
    int maxBackoffTimeout = 10;
    private final WebClient webClient;

    public PaymentClientService(@Value("${paymentservice.base-url}") String paymentServiceBaseUrl, WebClient.Builder webClientBuilder) {
        // Create an HttpClient with a response timeout
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));

        this.webClient = webClientBuilder
                .baseUrl(paymentServiceBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        log.debug("Payment Service base URL: '{}'", paymentServiceBaseUrl);
    }

    public Mono<PaymentDto> payOrder(Long id, PayOrderRequestDto orderDto) {
        log.debug("OrderDto {}", orderDto);
        return webClient.post()
                .uri("/payments")
                .bodyValue(orderDto)
                .retrieve()
//                .onStatus(HttpStatusCode::is4xxServerError, handle4xxError(id))
//                .onStatus(HttpStatusCode::is5xxServerError, handle5xxError(id))
                .bodyToMono(PaymentDto.class)
                .retryWhen(retrySpec(id))
                .onErrorMap(Exceptions::isRetryExhausted, mapThrowableToResponse(id));
    }

    private Function<ClientResponse, Mono<? extends Throwable>> handle4xxError(Long orderId) {
        return response -> {

            log.error("Client error from Payment Service ({}) for order ID {}.", response.statusCode(), orderId);
            // Return a new Mono that will trigger the retry.
            return Mono.error(new ResponseStatusException(response.statusCode(),
                    "Client error from Payment Service (4xx) for order ID " + orderId + "."));
        };
    }

    private Function<ClientResponse, Mono<? extends Throwable>> handle5xxError(Long orderId) {
        return response -> {
            log.error("Server error from Payment Service (5xx) for order ID {}. Retrying...", orderId);
            // Return a new Mono that will trigger the retry.
            return Mono.error(new ServiceUnavailableException(
                    "Server error from Payment Service (5xx) for order ID " + orderId + ". Retrying..."));
        };
    }

    private RetryBackoffSpec retrySpec(Long orderId) {
        return Retry.backoff(maxAttempts, Duration.ofSeconds(retryTimeout))
                .jitter(jitterFactor)
                .maxBackoff(Duration.ofSeconds(maxBackoffTimeout))
                .filter(throwable ->
                {
                    boolean accepted = switch (throwable) {
                        case WebClientResponseException.ServiceUnavailable ignored -> true;
                        case WebClientResponseException ignored -> true;
                        case ConnectException ignored -> true; // see JavaDoc
                        // other reasons to retry must return true
                        default -> false;
                    };
                    log.debug("Error {}, accepted: {}", throwable.getClass().getName(), accepted);
                    return accepted;
                })
                .doBeforeRetry(retrySignal -> log.warn("Retrying fetch for order ID {}...", orderId));
    }

    private Function<Throwable, Throwable> mapThrowableToResponse(Long orderId) {
        return throwable -> {
            log.error("Failed to retrieve order ID {} after multiple retries.", orderId);
            return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Payment service is currently unavailable. Please try again later.");
        };
    }

}
