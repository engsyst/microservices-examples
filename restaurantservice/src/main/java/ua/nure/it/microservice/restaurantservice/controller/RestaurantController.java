package ua.nure.it.microservice.restaurantservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.it.microservice.restaurantservice.dto.RestaurantDto;
import ua.nure.it.microservice.restaurantservice.entity.Restaurant;
import ua.nure.it.microservice.restaurantservice.service.RestaurantService;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping(produces = {"application/json", "application/xml"})
    public ResponseEntity<Page<RestaurantDto>> getAllRestaurants(@PageableDefault(page = 0, size = 3) Pageable pageable) {
        Page<RestaurantDto> restaurants = restaurantService.getAllRestaurants(pageable)
                .map(RestaurantDto::of);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping(path = "/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id)
                .map(RestaurantDto::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantDto restaurantDto) {
        Restaurant restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .address(restaurantDto.getAddress())
                .isOpen(restaurantDto.isOpen())
                .build();
        Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant);
        return ResponseEntity.ok(RestaurantDto.of(savedRestaurant));
    }

    @GetMapping(path = "/{id}/is-open", produces = {"application/json", "application/xml"})
    public ResponseEntity<Boolean> isRestaurantOpen(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.isRestaurantOpen(id));
    }
}