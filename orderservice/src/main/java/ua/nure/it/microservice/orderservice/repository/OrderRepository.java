package ua.nure.it.microservice.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.it.microservice.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
