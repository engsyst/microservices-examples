package ua.nure.it.microservice.restaurantservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nure.it.microservice.restaurantservice.entity.Restaurant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    private boolean isOpen;

    // Static mapping method from Restaurant entity to RestaurantDto
    public static RestaurantDto of(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .isOpen(restaurant.isOpen())
                .build();
    }
}
