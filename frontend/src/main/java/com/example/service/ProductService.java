package com.example.service;

import com.example.exception.ProductNotFoundException;
import com.example.model.Product;
import com.example.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class ProductService {

    private final RestTemplate restTemplate;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    @Autowired
    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ApiResponse<Product> getProduct(@PathVariable String productName) {
        String getProductUrl = catalogServiceUrl + "products/" + productName;
        try {
            // Fetch the product from catalog service
            return restTemplate.getForObject(getProductUrl, ApiResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException("Product not found: " + productName); // Custom exception
        } catch (Exception ex) {
            // Handle other possible errors
            System.out.println(ex);
            throw new RuntimeException("Internal server error");
        }
    }
}
