package com.example.controller;

import com.example.model.Product;
import com.example.model.ApiResponse;
import com.example.model.ErrorResponse;
import com.example.model.ErrorWrapper;
import com.example.service.ProductService;
import com.example.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{productName}")
    public ResponseEntity<?> getProduct(@PathVariable String productName) {
        try {
            Product product = productService.getProduct(productName);
            ApiResponse<Product> response = new ApiResponse<>(product);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(404, "Product not found: " + productName);
            ErrorWrapper errorWrapper = new ErrorWrapper(errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorWrapper);
        }
    }
}
