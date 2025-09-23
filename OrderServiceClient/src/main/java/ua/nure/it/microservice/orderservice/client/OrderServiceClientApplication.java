package ua.nure.it.microservice.orderservice.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
public class OrderServiceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceClientApplication.class, args);
    }
}