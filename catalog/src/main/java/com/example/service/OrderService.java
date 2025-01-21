package com.example.service;

import com.example.exception.InsufficientStockException;
import com.example.exception.OrderNotFoundException;
import com.example.exception.ProductNotFoundException;
import com.example.model.ApiResponse;
import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    public Order createOrder(Order order) throws InsufficientStockException {
        try {
            Product product = productService.getProduct(order.getName());
            if (product != null && product.getQuantity() >= order.getQuantity()) {
                product.setQuantity(product.getQuantity() - order.getQuantity());
                productRepository.save(product);
                return orderRepository.save(order);
            } else {
                throw new InsufficientStockException("Insufficient stock for product: " + order.getName());
            }
        } catch (ProductNotFoundException ex) {
            throw new ProductNotFoundException(ex.getMessage()); // Custom exception
        } catch (InsufficientStockException ex) {
            throw new InsufficientStockException(ex.getMessage()); // Custom exception
        } catch (RestClientException ex) {
            // Handle other possible errors
            throw new RuntimeException("Internal server error");
        }
    }

    public Order getOrderById(Long orderNumber) {
        try {
            return orderRepository.findById(orderNumber).orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderNumber));
        } catch (OrderNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            // Log and handle internal server errors
            throw new RuntimeException("Internal server error");
        }
    }
}
