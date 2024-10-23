package com.asollaorta.amex.api.controller;

import com.asollaorta.amex.api.models.Item;
import com.asollaorta.amex.api.models.Order;
import com.asollaorta.amex.api.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Set<Item> items) {
        Order order = orderService.makeOrder(items);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
