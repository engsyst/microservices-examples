package ua.nure.it.microservice.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.nure.it.microservice.orderservice.entity.Order;
import ua.nure.it.microservice.orderservice.entity.OrderStatus;
import ua.nure.it.microservice.orderservice.repository.OrderRepository;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantClientService restaurantClientService;
    // Inject the value from application.properties
    @Value("${order.default.status}")
    private String defaultOrderStatus;

    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order createOrder(Order order) {
        // Fetch prices for all menu items and calculate the total asynchronously
        BigDecimal totalPrice = order.getMenuItemIds().stream()
                .map(restaurantClientService::getMenuItemPrice)
                .toList()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setOrderStatus(OrderStatus.valueOf(defaultOrderStatus));
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    @Transactional
    public Order addMenuItemToOrder(Long orderId, Long menuItemId) {
        // Fetch the existing order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        BigDecimal menuItemPrice = restaurantClientService.getMenuItemPrice(menuItemId);
        order.setTotalPrice(order.getTotalPrice().add(menuItemPrice));
        return orderRepository.save(order);

    }

    private Function<BigDecimal, Order> getOrderTotalPrice(Long menuItemId, Order order) {
        return newPrice -> {
            // Add the new item to the list and update the total price
            order.getMenuItemIds().add(menuItemId);
            BigDecimal updatedPrice = order.getTotalPrice().add(newPrice);
            order.setTotalPrice(updatedPrice);
            return orderRepository.save(order);
        };
    }

    @Transactional
    public Order updateOrder(Order order) {
        orderRepository.save(order);
        return order;
    }
}
