package ua.nure.it.microservice.restaurantservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.it.microservice.restaurantservice.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
