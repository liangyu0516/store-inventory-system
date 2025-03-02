package com.example.service;

import com.example.exception.ProductNotFoundException;
import com.example.model.Product;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.annotation.PostConstruct;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    public ProductService() {
        products.add(new Product("Tux", 6.9, 100));
        products.add(new Product("Uno", 5.0, 100));
        products.add(new Product("Clue", 15.0, 100));
        products.add(new Product("Lego", 23.3, 100));
        products.add(new Product("Chess", 17.5, 100));
        products.add(new Product("Barbie", 10.0, 100));
        products.add(new Product("Bubbles", 2.75, 100));
        products.add(new Product("Frisbee", 8.8, 100));
        products.add(new Product("Twister", 13.3, 100));
        products.add(new Product("Elephant", 20.0, 100));
    }

    @PostConstruct
    @Scheduled(fixedRate = 10000) // 10 seconds
    public void postConstructRoutine() {
        for (Product product : products) {
            productRepository.save(product);
        }
    }

    public Product getProduct(@PathVariable String productName) {
        try {
            return productRepository.findByName(productName).orElseThrow(() -> new ProductNotFoundException("Product not found: " + productName));
        } catch (ProductNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            // Log and handle internal server errors
            throw new RuntimeException("Internal server error");
        }
    }
}
