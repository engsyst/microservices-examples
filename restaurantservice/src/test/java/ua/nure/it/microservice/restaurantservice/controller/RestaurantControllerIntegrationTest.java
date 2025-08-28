package ua.nure.it.microservice.restaurantservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
import ua.nure.it.microservice.restaurantservice.dto.RestaurantDto;
import ua.nure.it.microservice.restaurantservice.entity.Restaurant;
import ua.nure.it.microservice.restaurantservice.repository.RestaurantRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Ensures the database is reset for each test
@TestPropertySource(locations = "classpath:application-test.properties") // Use a separate config for tests
public class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Used for JSON serialization

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurant1;

    // Use a separate XmlMapper for XML serialization
    private final XmlMapper xmlMapper = new XmlMapper();

    @BeforeEach
    public void setUp() {
        // Clear all restaurants before each test to ensure a clean state
        restaurantRepository.deleteAll();

        // Create and save some test data
        restaurant1 = new Restaurant();
        restaurant1.setName("Pizza Planet");
        restaurant1.setAddress("123 Pizza St");
        restaurant1.setOpen(true);
        restaurantRepository.save(restaurant1);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Taco Time");
        restaurant2.setAddress("456 Taco Rd");
        restaurant2.setOpen(false);
        restaurantRepository.save(restaurant2);
    }

    @Test
    public void testGetAllRestaurants() throws Exception {
        // Perform a GET request to the /restaurants endpoint
        mockMvc.perform(get("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Pizza Planet"))
                .andExpect(jsonPath("$.content[1].name").value("Taco Time"));
    }

    @Test
    public void testGetRestaurantById_Success() throws Exception {
        // Perform a GET request for a valid restaurant ID
        mockMvc.perform(get("/restaurants/{id}", restaurant1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(restaurant1.getId()))
                .andExpect(jsonPath("$.name").value("Pizza Planet"))
                .andExpect(jsonPath("$.address").value("123 Pizza St"))
                .andExpect(jsonPath("$.open").value(true));
    }

    @Test
    public void testGetRestaurantById_NotFound() throws Exception {
        // Perform a GET request for an invalid restaurant ID
        mockMvc.perform(get("/restaurants/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateRestaurant_Success() throws Exception {
        // Create a new restaurant DTO
        RestaurantDto newRestaurantDto = RestaurantDto.builder()
                .name("New Bistro")
                .address("789 Food Lane")
                .isOpen(true)
                .build();

        // Perform a POST request to create the restaurant
        MvcResult result = mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRestaurantDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Bistro"))
                .andExpect(jsonPath("$.address").value("789 Food Lane"))
                .andExpect(jsonPath("$.open").value(true))
                .andReturn();

        // Verify that the restaurant was actually saved in the database
        Long createdId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        Optional<Restaurant> savedRestaurant = restaurantRepository.findById(createdId);
        assertThat(savedRestaurant).isPresent();
        assertThat(savedRestaurant.get().getName()).isEqualTo("New Bistro");
    }

    @Test
    public void testCreateRestaurant_BlankName_Failure() throws Exception {
        // Create a restaurant with a blank name
        RestaurantDto invalidDto = RestaurantDto.builder()
                .name("")
                .address("123 Test St")
                .isOpen(true)
                .build();

        // Perform a POST request and expect a bad request status
        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testMediaTypeSupport() throws Exception {
        // JSON Test Case
        RestaurantDto jsonRestaurantDto = RestaurantDto.builder()
                .name("JSON Eatery")
                .address("111 Json St")
                .isOpen(true)
                .build();

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonRestaurantDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("JSON Eatery"));

        // XML Test Case
        RestaurantDto xmlRestaurantDto = RestaurantDto.builder()
                .name("XML Cafe")
                .address("222 Xml Ave")
                .isOpen(true)
                .build();

        String xmlPayload = xmlMapper.writeValueAsString(xmlRestaurantDto);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(xmlPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(xpath("/RestaurantDto/name").string("XML Cafe"));
    }
}
