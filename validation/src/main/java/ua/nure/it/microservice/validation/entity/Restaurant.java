package ua.nure.it.microservice.validation.entity;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Restaurant {
    private Long id;
    private String name;
    private String address;
    private boolean isOpen;
    private List<MenuItem> menuItems;
}