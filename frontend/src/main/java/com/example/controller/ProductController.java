package com.example.controller;

import com.example.model.Product;
import com.example.model.ApiResponse;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProductController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{productName}")
    public ResponseEntity<?> getProduct(@PathVariable String productName) {
        return ResponseEntity.ok(productService.getProduct(productName));
    }
}
