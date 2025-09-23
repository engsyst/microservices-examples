package ua.nure.it.microservice.orderservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.nure.it.microservice.orderservice.client.api.OrderControllerApi;

@Configuration
public class ApiConfig {

    @Bean
    public ApiClient getApiClient(@Value("${orderservice.base-url:http://localhost:8082}") String baseUrl) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);
        return apiClient;
    }

    @Bean
    public OrderControllerApi getOrderControllerApi(ApiClient client) {
        return new OrderControllerApi(client);
    }
}
