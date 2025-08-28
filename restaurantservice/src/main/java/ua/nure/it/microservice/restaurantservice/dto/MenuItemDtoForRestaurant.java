package ua.nure.it.microservice.restaurantservice.dto;

import lombok.*;
import ua.nure.it.microservice.restaurantservice.entity.MenuItem;

import java.math.BigDecimal;

// MenuItem DTO when listed within a Restaurant (doesn't include the 'restaurant' field)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDtoForRestaurant {
    private Long id;
    private String name;
    private BigDecimal price;

    // Static mapping method from MenuItem entity to MenuItemDtoForRestaurant
    public static MenuItemDtoForRestaurant of(MenuItem menuItem) {
        if (menuItem == null) {
            return null;
        }
        return MenuItemDtoForRestaurant.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .price(menuItem.getPrice())
                .build();
    }
}
