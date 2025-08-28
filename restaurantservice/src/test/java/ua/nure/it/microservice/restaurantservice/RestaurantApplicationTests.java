package ua.nure.it.microservice.restaurantservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class RestaurantApplicationTests {

	@Test
	void contextLoads() {
	}

}
