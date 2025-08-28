package ua.nure.it.microservice.restaurantservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nure.it.microservice.restaurantservice.entity.MenuItem;
import ua.nure.it.microservice.restaurantservice.repository.MenuItemRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    final MenuItemRepository menuItemRepository;
    
    @Transactional(readOnly = true)
    public Page<MenuItem> getAllMenuItems(Pageable pageable) {
        return menuItemRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    @Transactional
    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }
}
