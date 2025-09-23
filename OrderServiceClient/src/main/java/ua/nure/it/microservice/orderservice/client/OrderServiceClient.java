package ua.nure.it.microservice.orderservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.it.microservice.orderservice.client.api.OrderControllerApi;
import ua.nure.it.microservice.orderservice.client.model.CreateOrderDto;
import ua.nure.it.microservice.orderservice.client.model.OrderDto;
import ua.nure.it.microservice.orderservice.client.model.Pageable;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class OrderServiceClient implements CommandLineRunner {

    final OrderControllerApi client;

    @Autowired
    public OrderServiceClient(OrderControllerApi client) {
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {


        OrderDto order1 = null;
        try {
            order1 = Optional.ofNullable(client
                            .createOrder(new CreateOrderDto(List.of(1L, 9L), "address One"))
                    )
                    .orElseThrow(() -> {
                        log.error("Not found");
                        return new RuntimeException("not found");
                    });
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        System.out.printf("Order1: %s", order1);
        try {
            OrderDto order2 = Optional.ofNullable(client
                            .createOrder(new CreateOrderDto(List.of(2L, 9L), "address Two")))
                    .orElseThrow(() -> {
                        log.error("Not found");
                        return new RuntimeException("not found");
                    });
            System.out.printf("Order2: %s%n", order2);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }

        // Getting all existing orders
        try {
            Optional.ofNullable(client.getAllOrders(new Pageable()))
                    .orElseThrow(() -> new RuntimeException("Not found any orders."))
                    .getContent()
                    .forEach(System.out::println);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }

        // Pay order
        OrderDto paidOrder1 = null;
        try {
            paidOrder1 = Optional.ofNullable(client
                            .payOrder(order1.getId()))
                    .orElseThrow(() -> {
                        log.error("not found");
                        return new RuntimeException("not found");
                    });
            System.out.println(paidOrder1);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }

        // Get last paid order
        OrderDto lastPaidOrder = client.getOrderById(paidOrder1.getId());
        System.out.printf("Last paid order: %s%n", lastPaidOrder);
        OrderDto secondlyPaidOrder = client.getOrderById(paidOrder1.getId());
        System.out.printf("Secondly paid order: %s%n", secondlyPaidOrder);
    }
}
