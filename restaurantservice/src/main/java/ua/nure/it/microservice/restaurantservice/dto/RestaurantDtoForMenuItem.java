package ua.nure.it.microservice.restaurantservice.dto;

import lombok.*;
import ua.nure.it.microservice.restaurantservice.entity.Restaurant;

// Restaurant DTO when listed within a MenuItem (doesn't include the 'menuItems' list)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDtoForMenuItem {
    private Long id;
    private String name;
    private String address;
    private boolean isOpen;

    // Static mapping method from Restaurant entity to RestaurantDtoForMenuItem
    public static RestaurantDtoForMenuItem of(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }
        return RestaurantDtoForMenuItem.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .isOpen(restaurant.isOpen())
                .build();
    }

    public Restaurant toEntity() {
        return new Restaurant(id, name, address, isOpen, null);
    }

}

