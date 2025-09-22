package ua.nure.it.microservice.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import ua.nure.it.microservice.orderservice.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<Long> menuItemIds; // IDs of menu items

    private BigDecimal totalPrice;
    private String deliveryAddress;
    @Value("${order.default.status}")
    private OrderStatus orderStatus;
    private Long paymentId;
}