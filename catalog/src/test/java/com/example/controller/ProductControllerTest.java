package com.example.controller;

import com.example.model.ApiResponse;
import com.example.model.ErrorResponse;
import com.example.model.ErrorWrapper;
import com.example.model.Product;
import com.example.service.ProductService;
import com.example.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void testGetProduct_Success() throws Exception {
        // Arrange
        String productName = "Tux";
        double productPrice = 6.9;
        int productQuantity = 100;
        Product product = new Product(productName, productPrice, productQuantity);
        when(productService.getProduct(productName)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/products/{productName}", productName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(productName))
                .andExpect(jsonPath("$.data.price").value(productPrice))
                .andExpect(jsonPath("$.data.quantity").value(productQuantity));
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
        when(productService.getProduct(productName)).thenThrow(new RuntimeException("Internal server error"));

        // Act & Assert
        mockMvc.perform(get("/products/{productName}", productName))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code").value(500))
                .andExpect(jsonPath("$.error.message").value("Internal server error"));
    }
}
