package ua.nure.it.microservice.restaurantservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.it.microservice.restaurantservice.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}