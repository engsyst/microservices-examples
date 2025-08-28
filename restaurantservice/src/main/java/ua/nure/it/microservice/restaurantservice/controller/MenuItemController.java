package ua.nure.it.microservice.restaurantservice.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.it.microservice.restaurantservice.dto.MenuItemDto;
import ua.nure.it.microservice.restaurantservice.entity.MenuItem;
import ua.nure.it.microservice.restaurantservice.service.MenuItemService;

import java.net.URI;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Slf4j
public class MenuItemController {
    private final MenuItemService menuItemService;


    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public Page<MenuItemDto> getAllMenuItems(
            @PageableDefault(page = 0, size = 10, sort = "name")
            Pageable pageable) {
        return menuItemService.getAllMenuItems(pageable).map(MenuItemDto::of);
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<MenuItemDto> getMenuItemById(@PathVariable Long id) {
        MenuItemDto menuItemDto = menuItemService.getMenuItemById(id)
                .map(MenuItemDto::of)
                .orElseThrow(() -> new EntityNotFoundException("Menu Item not found with id: " + id));
        log.debug("MenuItem: {}", menuItemDto);
        return ResponseEntity.ok(menuItemDto);
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<MenuItemDto> createMenuItem(@Valid @RequestBody MenuItemDto menuItemDto) {
        MenuItem savedItem = menuItemService.createMenuItem(menuItemDto.toEntity());
        MenuItemDto responseDto = MenuItemDto.of(savedItem);
        URI location = URI.create("/menu/" + responseDto.getId());

        return ResponseEntity
                .created(location)
                .body(responseDto);
    }
}
