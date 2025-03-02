package com.example.controller;

import com.example.exception.InsufficientStockException;
import com.example.exception.OrderNotFoundException;
import com.example.exception.ProductNotFoundException;
import com.example.model.ApiResponse;
import com.example.model.Order;
import com.example.model.OrderNumber;
import com.example.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange
        OrderNumber orderNumber = new OrderNumber(1L);
        ApiResponse<OrderNumber> apiResponse = new ApiResponse<>(orderNumber);
        when(orderService.createOrder(Mockito.any(Order.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Tux\", \"quantity\": 10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber.getOrderNumber()));
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
        ApiResponse<Order> apiResponse = new ApiResponse<>(order);
        when(orderService.getOrderById(Mockito.any(Long.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(get("/orders/{orderNumber}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(order.getId()))
                .andExpect(jsonPath("$.data.name").value(order.getName()))
                .andExpect(jsonPath("$.data.quantity").value(order.getQuantity()));
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
