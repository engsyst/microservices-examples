package ua.nure.it.microservice.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ua.nure.it.microservice.orderservice.dto.MenuItemDtoForOrderService;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service to interact with the RestaurantService.
 * Uses a reactive WebClient for non-blocking HTTP calls.
 */
@Service
@Slf4j
public class RestaurantClientService {

    private final RestClient restClient;

    public RestaurantClientService(@Value("${restaurantservice.base-url}") String restaurantServiceBaseUrl,
                                   RestClient.Builder restClient) {
//        this.restaurantServiceBaseUrl = restaurantServiceBaseUrl;
        this.restClient = restClient.baseUrl(restaurantServiceBaseUrl).build();
    }

    /**
     * Fetches the price of a single menu item from the restaurant service.<br><br>
     *
     * Common Errors to Handle with Retries<br>
     * A distributed system can fail for many reasons. Here are some of the most common errors
     * you should consider including in your retry logic:<br>
     * {@code ConnectionRefused}: This occurs when a client tries to connect to a server,
     * but the server actively rejects the connection attempt.
     * This is usually not a network issue itself, but a problem on the local server side.<br>
     * {@code ConnectTimeoutException}: This occurs when your application tries to open
     * a connection to the remote service, but the server does not respond within
     * the configured timeout period. It suggests the service might be too busy or
     * the network is congested.<br>
     * {@code SocketTimeoutException} or {@code ReadTimeoutException}: This happens after
     * a connection is established. It means the remote service did not send any data back
     * within the specified timeout, which can indicate the service is slow, hung, or overloaded.<br>
     * {@code 503 Service Unavailable}: This is a direct HTTP status code from the remote service
     * telling you that it is temporarily unable to handle the request. This is a clear signal
     * that a retry is appropriate, as the service is explicitly asking the client to try
     * again later.<br>
     * {@code 429 Too Many Requests}: This status code indicates that the service
     * is rate-limiting your requests. Retrying after a short, exponentially
     * increasing delay (exponential backoff) is the correct way to handle this.<br>
     * {@code UnknownHostException}: While less frequent, this can occur if
     * a service discovery mechanism is temporarily unavailable, or a DNS entry is stale.
     * A retry might resolve the issue once the name-to-address lookup succeeds.<br>
     * {@code 500 Internal Server Error}: In some cases, a 500 error can be transient
     * and caused by a temporary backend issue. While you should not retry all
     * 500 errors (as some are caused by permanent code issues), you might decide
     * to retry specific types of 500s.<br><br>
     * The key is to use a filter in your reactive retry specification
     * (like your retryWhen call) to specifically catch these types of transient
     * errors and not others. Retrying on non-transient errors, such as
     * 404 Not Found or 400 Bad Request, is pointless as the outcome will not change.<br>
     *
     * @param menuItemId The ID of the menu item.
     * @return A Mono containing the price of the menu item, or an empty Mono if not found.
     */
    public BigDecimal getMenuItemPrice(Long menuItemId) {
        return Optional.ofNullable(restClient
                        .get()
                        .uri("/menu/{id}", menuItemId)
                        .retrieve()
                        .body(MenuItemDtoForOrderService.class)
                ).orElseThrow(() -> new EntityNotFoundException("Menu item not found with ID: " + menuItemId))
                .getPrice();
    }
}