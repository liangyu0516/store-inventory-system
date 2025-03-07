package com.example.service;

import com.example.exception.OrderNotFoundException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.InsufficientStockException;
import com.example.model.ApiResponse;
import com.example.model.Order;
import com.example.model.OrderNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    private final RestTemplate restTemplate;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Autowired
    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ApiResponse<OrderNumber> createOrder(Order order) {
        String postOrderUrl = orderServiceUrl + "orders";
        try {
            return restTemplate.postForObject(postOrderUrl, order, ApiResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException("Product not found: " + order.getName());
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new InsufficientStockException("Insufficient stock for product: " + order.getName());
        } catch (RestClientException ex) {
            throw new RuntimeException("Internal server error");
        }
    }

    public ApiResponse<Order> getOrderById(@PathVariable Long orderNumber) {
        String getOrderUrl = orderServiceUrl + "orders/" + orderNumber;
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
