package com.example.service;

import com.example.exception.OrderNotFoundException;
import com.example.model.ApiResponse;
import com.example.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class OrderService {

    private final RestTemplate restTemplate;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    @Autowired
    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ApiResponse<Order> getOrderById(@PathVariable Long orderNumber) {
        String getOrderUrl = catalogServiceUrl + "orders/" + orderNumber;
        try {
            // Fetch the order from catalog service
            return restTemplate.getForObject(getOrderUrl, ApiResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new OrderNotFoundException("Order not found: " + orderNumber); // Custom exception
        } catch (RestClientException ex) {
            // Handle other possible errors
            throw new RuntimeException("Internal server error");
        }
    }
}
