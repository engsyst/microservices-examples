package ua.nure.it.microservice.restaurantservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nure.it.microservice.restaurantservice.entity.MenuItem;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDto {
    private Long id;
    @NotBlank
    private String name;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
    @NotNull
    private RestaurantDtoForMenuItem restaurant; // A simplified DTO for the parent restaurant

    // Static mapping method from MenuItem entity to MenuItemDto
    public static MenuItemDto of(MenuItem menuItem) {
        if (menuItem == null) {
            return null;
        }
        return MenuItemDto.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .price(menuItem.getPrice())
                .restaurant(RestaurantDtoForMenuItem.of(menuItem.getRestaurant()))
                .build();
    }

    public MenuItem toEntity() {
        MenuItem entity = new MenuItem();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setPrice(this.price);
        if (this.restaurant != null) {
            entity.setRestaurant(this.restaurant.toEntity());
        }
        return entity;
    }
}