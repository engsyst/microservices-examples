package ua.nure.it.microservice.validation.entity;


import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MenuItem {
    private Long id;
    private String name;
    private BigDecimal price;
    private Restaurant restaurant;
}
