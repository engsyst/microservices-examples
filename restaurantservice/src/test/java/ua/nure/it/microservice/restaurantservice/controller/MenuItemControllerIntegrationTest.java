package ua.nure.it.microservice.restaurantservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ua.nure.it.microservice.restaurantservice.dto.MenuItemDto;
import ua.nure.it.microservice.restaurantservice.dto.RestaurantDtoForMenuItem;
import ua.nure.it.microservice.restaurantservice.entity.MenuItem;
import ua.nure.it.microservice.restaurantservice.entity.Restaurant;
import ua.nure.it.microservice.restaurantservice.repository.MenuItemRepository;
import ua.nure.it.microservice.restaurantservice.repository.RestaurantRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class MenuItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant testRestaurant;
    private MenuItem menuItem1;
    private MenuItem menuItem2;

    @BeforeEach
    public void setUp() {
        // Clear repositories to ensure a clean state for each test
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();

        // Create and save a test restaurant, as menu items need a parent restaurant
        testRestaurant = new Restaurant();
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setAddress("123 Test St");
        testRestaurant.setOpen(true);
        restaurantRepository.save(testRestaurant);

        // Create and save a couple of menu items associated with the test restaurant
        menuItem1 = new MenuItem();
        menuItem1.setName("Pasta Carbonara");
        menuItem1.setPrice(new BigDecimal("15.50"));
        menuItem1.setRestaurant(testRestaurant);
        menuItemRepository.save(menuItem1);

        menuItem2 = new MenuItem();
        menuItem2.setName("Margherita Pizza");
        menuItem2.setPrice(new BigDecimal("12.00"));
        menuItem2.setRestaurant(testRestaurant);
        menuItemRepository.save(menuItem2);
    }

    @Test
    public void testGetAllMenuItems() throws Exception {
        // Perform a GET request to the /menu endpoint and verify the response
        mockMvc.perform(get("/menu")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Margherita Pizza"))
                .andExpect(jsonPath("$.content[1].name").value("Pasta Carbonara"));
    }

    @Test
    public void testGetMenuItemById_Success() throws Exception {
        // Perform a GET request for a valid menu item ID
        mockMvc.perform(get("/menu/{id}", menuItem1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(menuItem1.getId()))
                .andExpect(jsonPath("$.name").value("Pasta Carbonara"))
                .andExpect(jsonPath("$.price").value(15.50));
    }

    @Test
    public void testGetMenuItemById_NotFound() throws Exception {
        // Perform a GET request for a non-existent menu item ID
        mockMvc.perform(get("/menu/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateMenuItem_Success() throws Exception {
        // Create a new MenuItemDto to be sent in the request
        MenuItemDto newItemDto = MenuItemDto.builder()
                .name("Spaghetti Bolognese")
                .price(new BigDecimal("18.75"))
                .restaurant(RestaurantDtoForMenuItem.of(testRestaurant))
                .build();

        // Perform a POST request to create the menu item
        MvcResult result = mockMvc.perform(post("/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Spaghetti Bolognese"))
                .andExpect(jsonPath("$.price").value(18.75))
                .andReturn();

        // Verify that the menu item was actually saved in the database
        Long createdId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        Optional<MenuItem> savedMenuItem = menuItemRepository.findById(createdId);
        assertThat(savedMenuItem).isPresent();
        assertThat(savedMenuItem.get().getName()).isEqualTo("Spaghetti Bolognese");
    }

    @Test
    public void testCreateMenuItem_InvalidPayload_Failure() throws Exception {
        // Create an invalid MenuItemDto with a blank name
        MenuItemDto invalidItemDto = MenuItemDto.builder()
                .name("")
                .price(new BigDecimal("10.00"))
                .restaurant(RestaurantDtoForMenuItem.of(testRestaurant))
                .build();

        // Perform a POST request and expect a bad request status
        mockMvc.perform(post("/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateMenuItem_NegativePrice_Failure() throws Exception {
        // Create an invalid MenuItemDto with a negative price
        MenuItemDto invalidItemDto = MenuItemDto.builder()
                .name("Bad Item")
                .price(new BigDecimal("-5.00"))
                .restaurant(RestaurantDtoForMenuItem.of(testRestaurant))
                .build();

        // Perform a POST request and expect a bad request status
        mockMvc.perform(post("/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItemDto)))
                .andExpect(status().isBadRequest());
    }
}
