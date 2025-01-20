package com.example.controller;

import com.example.exception.InsufficientStockException;
import com.example.exception.ProductNotFoundException;
import com.example.model.*;
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
        try {
            Order savedOrder = orderService.createOrder(order);
            ApiResponse<OrderNumber> response = new ApiResponse<>(new OrderNumber(savedOrder.getId()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ProductNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(404, ex.getMessage());
            ErrorWrapper errorWrapper = new ErrorWrapper(errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorWrapper);
        } catch (InsufficientStockException ex) {
            ErrorResponse errorResponse = new ErrorResponse(400, "Insufficient stock for product: " + order.getName());
            ErrorWrapper errorWrapper = new ErrorWrapper(errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorWrapper);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(500, ex.getMessage());
            ErrorWrapper errorWrapper = new ErrorWrapper(errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWrapper);
        }


    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderNumber) {
        Optional<Order> orderOptional = orderService.getOrderById(orderNumber);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            ApiResponse<Order> response = new ApiResponse<>(order);
            System.out.println(response);
            return ResponseEntity.ok(response);
        } else {
            ErrorResponse errorResponse = new ErrorResponse(404, "Order not found: " + orderNumber);
            ErrorWrapper errorWrapper = new ErrorWrapper(errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorWrapper);
        }
    }
}
