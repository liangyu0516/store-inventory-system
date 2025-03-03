package com.example.controller;

import com.example.model.ApiResponse;
import com.example.model.Product;
import com.example.service.ProductService;
import com.example.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void testGetProduct_Success() throws Exception {
        // Arrange
        Product product = new Product("Tux", 6.9, 100);
        ApiResponse<Product> apiResponse = new ApiResponse<>(product);
        when(productService.getProduct(product.getName())).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(get("/products/{productName}", product.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(product.getName()))
                .andExpect(jsonPath("$.data.price").value(product.getPrice()))
                .andExpect(jsonPath("$.data.quantity").value(product.getQuantity()));
    }

    @Test
    void testGetProduct_NotFound() throws Exception {
        // Arrange
        String productName = "NonExistentProduct";
        when(productService.getProduct(productName)).thenThrow(new ProductNotFoundException("Product not found: " + productName));

        // Act & Assert
        mockMvc.perform(get("/products/{productName}", productName))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value(404))
                .andExpect(jsonPath("$.error.message").value("Product not found: " + productName));
    }

    @Test
    void testGetProduct_InternalServerError() throws Exception {
        // Arrange
        String productName = "Tux";
        when(productService.getProduct(any(String.class))).thenThrow(new RuntimeException("Internal server error"));

        // Act & Assert
        mockMvc.perform(get("/products/{productName}", productName))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("Internal server error"));
    }
}
