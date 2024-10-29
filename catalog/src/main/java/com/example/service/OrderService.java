package com.example.service;

import com.example.exception.InsufficientStockException;
import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Product product = productService.getProduct(order.getName());
        if (product != null && product.getQuantity() >= order.getQuantity()) {
            product.setQuantity(product.getQuantity() - order.getQuantity());
            productRepository.save(product);
            return orderRepository.save(order);
        } else {
            throw new InsufficientStockException("Insufficient stock for product: " + order.getName());
        }
    }

    public Optional<Order> getOrderById(Long orderNumber) {
        return orderRepository.findById(orderNumber);
    }
}
