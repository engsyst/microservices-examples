package ua.nure.it.microservice.restaurantservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.nure.it.microservice.restaurantservice.entity.Restaurant;
import ua.nure.it.microservice.restaurantservice.repository.RestaurantRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public boolean isRestaurantOpen(Long id) {
        return restaurantRepository.findById(id)
                .map(Restaurant::isOpen)
                .orElse(false);
    }
}