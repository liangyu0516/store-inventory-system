package com.example.controller;

import com.example.exception.InsufficientStockException;
import com.example.exception.OrderNotFoundException;
import com.example.exception.ProductNotFoundException;
import com.example.model.*;
import com.example.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange
        Order order = new Order("Tux", 10);
        order.setId(1L);
        when(orderService.createOrder(Mockito.any(Order.class))).thenReturn(order);

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Tux\", \"quantity\": 10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.orderNumber").value(order.getId()));
    }

    @Test
    void testCreateOrder_ProductNotFound() throws Exception {
        // Arrange
        String productName = "NonExistentProduct";
        when(orderService.createOrder(Mockito.any(Order.class))).thenThrow(new ProductNotFoundException("Product not found: " + productName));

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"NonExistentProduct\", \"quantity\": 10}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value(404))
                .andExpect(jsonPath("$.error.message").value("Product not found: NonExistentProduct"));
    }

    @Test
    void testCreateOrder_InsufficientStock() throws Exception {
        // Arrange
        String productName = "Tux";
        when(orderService.createOrder(Mockito.any(Order.class))).thenThrow(new InsufficientStockException("Insufficient stock for product: " + productName));

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Tux\", \"quantity\": 1000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(400))
                .andExpect(jsonPath("$.error.message").value("Insufficient stock for product: Tux"));
    }

    @Test
    void testCreateOrder_InternalServerError() throws Exception {
        // Arrange
        when(orderService.createOrder(Mockito.any(Order.class))).thenThrow(new RuntimeException("Internal server error"));

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Tux\", \"quantity\": 10}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("Internal server error"));
    }

    @Test
    void testGetOrder_Success() throws Exception {
        // Arrange
        Long orderId = 1L;
        Order order = new Order("Tux", 10);
        order.setId(orderId);
        when(orderService.getOrderById(orderId)).thenReturn(order);

        // Act & Assert
        mockMvc.perform(get("/orders/{orderNumber}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Tux"))
                .andExpect(jsonPath("$.data.quantity").value(10))
                .andExpect(jsonPath("$.data.id").value(orderId));
    }

    @Test
    void testGetOrder_OrderNotFound() throws Exception {
        // Arrange
        Long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenThrow(new OrderNotFoundException("Order not found: " + orderId));

        // Act & Assert
        mockMvc.perform(get("/orders/{orderNumber}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value(404))
                .andExpect(jsonPath("$.error.message").value("Order not found: 1"));
    }

    @Test
    void testGetOrder_InternalServerError() throws Exception {
        // Arrange
        Long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenThrow(new RuntimeException("Internal server error"));

        // Act & Assert
        mockMvc.perform(get("/orders/{orderNumber}", orderId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("Internal server error"));
    }
}
