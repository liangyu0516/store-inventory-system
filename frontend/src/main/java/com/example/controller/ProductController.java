package com.example.controller;

import com.example.exception.ProductNotFoundException;
import com.example.model.ErrorResponse;
import com.example.model.ErrorWrapper;
import com.example.model.Product;
import com.example.model.ApiResponse;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProductController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{productName}")
    public ResponseEntity<?> getProduct(@PathVariable String productName) {
        try {
            ApiResponse<Product> response = productService.getProduct(productName);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(404, ex.getMessage());
            ErrorWrapper errorWrapper = new ErrorWrapper(errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorWrapper);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(500, ex.getMessage());
            ErrorWrapper errorWrapper = new ErrorWrapper(errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorWrapper);
        }
    }
}
