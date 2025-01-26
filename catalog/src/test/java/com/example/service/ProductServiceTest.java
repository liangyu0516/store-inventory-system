package com.example.service;

import com.example.exception.ProductNotFoundException;
import com.example.model.Product;
import com.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProduct_Success() {
        // Arrange
        String productName = "Tux";
        Product mockProduct = new Product("Tux", 6.9, 100);
        when(productRepository.findByName(productName)).thenReturn(Optional.of(mockProduct));

        // Act
        Product result = productService.getProduct(productName);

        // Assert
        assertEquals("Tux", result.getName());
        assertEquals(6.9, result.getPrice());
        assertEquals(100, result.getQuantity());
    }

    @Test
    void testGetProduct_ProductNotFound() {
        // Arrange
        String productName = "NonExistentProduct";
        when(productRepository.findByName(productName)).thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProduct(productName);
        });

        assertEquals("Product not found: NonExistentProduct", exception.getMessage());
    }

    @Test
    void testGetProduct_InternalServerError() {
        // Arrange
        String productName = "Tux";
        when(productRepository.findByName(productName)).thenThrow(new RuntimeException());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProduct(productName);
        });

        assertEquals("Internal server error", exception.getMessage());
    }
}
