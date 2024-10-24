package com.example.controller;

import com.example.model.Product;
import com.example.model.ErrorResponse;
import com.example.service.ProductService;
import com.example.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/products/{productName}", method = RequestMethod.GET)
    public ResponseEntity<?> getProduct(@PathVariable String productName) {
        try {
            Product product = productService.getProduct(productName);
            return ResponseEntity.ok(product);
        } catch (ProductNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse("Product not found: " + productName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
