package com.example.service;

import com.example.exception.ProductNotFoundException;
import com.example.model.ApiResponse;
import com.example.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(ProductService.class)
class ProductServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(restTemplate);
    }

    @Test
    void testGetProduct_Success() {
        // Arrange
        String productName = "Tux";
        Product product = new Product("Tux", 6.9, 100);
        ApiResponse<Product> apiResponse = new ApiResponse<>(product);
        when(restTemplate.getForObject(any(String.class), eq(ApiResponse.class))).thenReturn(apiResponse);

        // Act & Assert
        ApiResponse<Product> result = productService.getProduct(productName);
        assertEquals(product.getName(), result.getData().getName());
        assertEquals(product.getPrice(), result.getData().getPrice());
        assertEquals(product.getQuantity(), result.getData().getQuantity());
    }

    @Test
    void testGetProduct_ProductNotFound() {
        // Arrange
        String productName = "NonExistentProduct";
        when(restTemplate.getForObject(any(String.class), eq(ApiResponse.class))).thenThrow(HttpClientErrorException.NotFound.class);

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
        when(restTemplate.getForObject(any(String.class), eq(ApiResponse.class))).thenThrow(RestClientException.class);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProduct(productName);
        });
        assertEquals("Internal server error", exception.getMessage());
    }
}
