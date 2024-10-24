package com.asollaorta.amex.api.controller;

import com.asollaorta.amex.api.models.ItemDto;
import com.asollaorta.amex.api.models.Order;
import com.asollaorta.amex.api.models.OrderDto;
import com.asollaorta.amex.api.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody Set<ItemDto> items) {
        OrderDto order = orderService.makeOrder(items);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<OrderDto>> getAllOrders() {

        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderDto orderDto = orderService.getOrderById(id);
        return ResponseEntity.ok(orderDto);
    }
}
