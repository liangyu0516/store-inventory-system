package com.example.controller;

import com.example.model.Order;
import com.example.model.OrderNumber;
import com.example.model.ApiResponse;
import com.example.model.ErrorResponse;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.createOrder(order);
        ApiResponse<OrderNumber> response = new ApiResponse<>(new OrderNumber(savedOrder.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderNumber) {
        Optional<Order> orderOptional = orderService.getOrderById(orderNumber);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            ApiResponse<Order> response = new ApiResponse<>(order);
            return ResponseEntity.ok(response);
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Order not found: " + orderNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
