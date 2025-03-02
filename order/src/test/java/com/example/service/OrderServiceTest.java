package com.example.service;

import com.example.exception.InsufficientStockException;
import com.example.exception.OrderNotFoundException;
import com.example.exception.ProductNotFoundException;
import com.example.model.ApiResponse;
import com.example.model.Order;
import com.example.model.OrderNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(OrderService.class)
class OrderServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(restTemplate);
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        OrderNumber orderNumber = new OrderNumber(1L);
        Order order = new Order("Tux", 10);
        ApiResponse<OrderNumber> apiResponse = new ApiResponse<>(orderNumber);
        when(restTemplate.postForObject(any(String.class), any(Order.class), eq(ApiResponse.class))).thenReturn(apiResponse);

        // Act & Assert
        ApiResponse<OrderNumber> result = orderService.createOrder(order);
        assertEquals(1L, result.getData().getOrderNumber());
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        // Arrange
        Order order = new Order("NonExistentProduct", 5);
        when(restTemplate.postForObject(any(String.class), any(Order.class), eq(ApiResponse.class))).thenThrow(HttpClientErrorException.NotFound.class);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(order));
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        // Arrange
        Order order = new Order("Tux", 1000);
        when(restTemplate.postForObject(any(String.class), any(Order.class), eq(ApiResponse.class))).thenThrow(HttpClientErrorException.BadRequest.class);

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(order));
    }

    @Test
    void testCreateOrder_InternalServerError() {
        // Arrange
        Order order = new Order("Tux", 1000);
        when(restTemplate.postForObject(any(String.class), any(Order.class), eq(ApiResponse.class))).thenThrow(RestClientException.class);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        Long orderNumber = 1L;
        Order order = new Order("Tux", 10);
        order.setId(orderNumber);
        ApiResponse<Order> response = new ApiResponse<>(order);
        when(restTemplate.getForObject(any(String.class), eq(ApiResponse.class))).thenReturn(response);

        // Act & Assert
        ApiResponse<Order> result = orderService.getOrderById(orderNumber);
        assertEquals(order.getId(), result.getData().getId());
        assertEquals(order.getName(), result.getData().getName());
        assertEquals(order.getQuantity(), result.getData().getQuantity());
    }

    @Test
    void testGetOrderById_OrderNotFound() {
        // Arrange
        Long orderNumber = 99L;
        when(restTemplate.getForObject(any(String.class), eq(ApiResponse.class))).thenThrow(HttpClientErrorException.NotFound.class);

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderNumber));
    }

    @Test
    void testGetOrderById_InternalServerError() {
        // Arrange
        Long orderNumber = 1L;
        when(restTemplate.getForObject(any(String.class), eq(ApiResponse.class))).thenThrow(RestClientException.class);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(orderNumber));
    }
}
