package com.example.service;

import com.example.exception.InsufficientStockException;
import com.example.exception.OrderNotFoundException;
import com.example.exception.ProductNotFoundException;
import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_Success() throws InsufficientStockException {
        // Arrange
        String productName = "Tux";
        int productQuantity = 100;
        Long orderId = 1L;
        int orderQuantity = 10;

        Product mockProduct = new Product(productName, 6.9, productQuantity);
        Order mockOrder = new Order(productName, orderQuantity);
        mockOrder.setId(orderId);

        when(productService.getProduct(productName)).thenReturn(mockProduct);
        when(orderRepository.save(mockOrder)).thenReturn(mockOrder);

        // Act
        Order result = orderService.createOrder(mockOrder);

        // Assert
        assertEquals(orderId, result.getId());
        assertEquals(productName, result.getName());
        assertEquals(orderQuantity, result.getQuantity());
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        // Arrange
        String productName = "NonExistentProduct";
        int orderQuantity = 10;
        Order mockOrder = new Order(productName, orderQuantity);

        when(productService.getProduct(productName)).thenThrow(new ProductNotFoundException("Product not found: " + productName));

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            orderService.createOrder(mockOrder);
        });

        assertEquals("Product not found: NonExistentProduct", exception.getMessage());
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        // Arrange
        String productName = "Tux";
        Product mockProduct = new Product(productName, 6.9, 100);
        Order mockOrder = new Order(productName, 1000);

        when(productService.getProduct(productName)).thenReturn(mockProduct);

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(mockOrder);
        });

        assertEquals("Insufficient stock for product: Tux", exception.getMessage());
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = new Order("Tux", 10);
        mockOrder.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // Act
        Order result = orderService.getOrderById(orderId);

        // Assert
        assertEquals(orderId, result.getId());
        assertEquals("Tux", result.getName());
        assertEquals(10, result.getQuantity());
    }

    @Test
    void testGetOrderById_OrderNotFound() {
        // Arrange
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(orderId);
        });

        assertEquals("Order not found: 1", exception.getMessage());
    }
}