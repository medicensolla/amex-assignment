package com.asollaorta.amex.api.services;

import com.asollaorta.amex.api.models.Item;
import com.asollaorta.amex.api.models.Order;
import com.asollaorta.amex.api.repositories.ItemRepository;
import com.asollaorta.amex.api.repositories.OrderRepository;
import com.asollaorta.amex.api.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;


    public Order makeOrder(Set<Item> items){

        if (items == null || items.isEmpty()) {
            throw new ApiException("Order must contain at least one item.",
                    HttpStatus.BAD_REQUEST, ZonedDateTime.now());
        }

        Set<Item> validItems = items.stream()
                .filter(item -> itemRepository.existsById(item.getId()))
                .collect(Collectors.toSet());

        if (validItems.isEmpty()) {
            throw new ApiException("None of the items exist in the inventory.",
                    HttpStatus.BAD_REQUEST,
                    ZonedDateTime.now());
        }

        Order order = new Order();
        order.setFinalCost(this.finalCostCalculator(validItems));
        order.setItems(validItems);

        return orderRepository.save(order);
    }


    private BigDecimal finalCostCalculator(Set<Item> validItems){

        BigDecimal totalCost = BigDecimal.ZERO;

        for (Item item : validItems) {
            BigDecimal itemTotal = item.getCost().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalCost = totalCost.add(itemTotal);
        }

        return totalCost;

    }




}
