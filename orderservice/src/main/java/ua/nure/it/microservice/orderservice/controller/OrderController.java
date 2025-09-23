package ua.nure.it.microservice.orderservice.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ua.nure.it.microservice.orderservice.dto.CreateOrderDto;
import ua.nure.it.microservice.orderservice.dto.OrderDto;
import ua.nure.it.microservice.orderservice.dto.PayOrderRequestDto;
import ua.nure.it.microservice.orderservice.entity.Order;
import ua.nure.it.microservice.orderservice.entity.OrderStatus;
import ua.nure.it.microservice.orderservice.service.OrderService;
import ua.nure.it.microservice.orderservice.service.PaymentClientService;

import java.net.URI;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final PaymentClientService paymentClientService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        Page<OrderDto> orders = orderService.getAllOrders(pageable).map(OrderDto::of);
        return ResponseEntity.ok(orders);
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id).orElseThrow((() -> new EntityNotFoundException("Order not found with id: " + id)));
        return ResponseEntity.ok(OrderDto.of(order));
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        // The service returns a Mono<Order>, so we map it to a Mono<ResponseEntity>
        Order order = orderService.createOrder(createOrderDto.toEntity());
        return ResponseEntity.created(URI.create("/orders/" + order.getId()))
                .body(OrderDto.of(order));
    }

    @PostMapping(
            path = "/{id}/menu/{menuItemId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<OrderDto> addMenuItemToOrder(@PathVariable Long id, @PathVariable Long menuItemId) {
        return ResponseEntity.ok(OrderDto.of(orderService.addMenuItemToOrder(id, menuItemId)));
    }


    @PostMapping(
            path = "/{id}/pay",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public Mono<ResponseEntity<OrderDto>> payOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order ID " + id + " was not found."));
        PayOrderRequestDto orderDto = PayOrderRequestDto.of(order);
        return paymentClientService.payOrder(id, orderDto)
                .flatMap(paymentDto -> {
                    if (!"SUCCESS".equals(paymentDto.getPaymentStatus())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                                "Probably Payment Service is unavailable, try again later"));
                    }
                    order.setOrderStatus(OrderStatus.PAID);
                    order.setPaymentId(paymentDto.getId());
                    orderService.updateOrder(order);
                    return Mono.just(ResponseEntity.ok().body(OrderDto.of(order)));
                });
    }

//    @PutMapping(
//            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
//    )
//    public ResponseEntity<OrderDto> updateOrder(@RequestBody OrderDto orderDto) {
//        orderDto = orderService.updateOrder(orderDto);
//        log.info("Status of order {} has been updated to {}", orderDto.getId(), orderDto.getOrderStatus());
//        return ResponseEntity.ok(orderDto);
//    }

}
