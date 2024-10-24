package com.asollaorta.amex.api.services;

import com.asollaorta.amex.api.exceptions.ApiException;
import com.asollaorta.amex.api.models.Item;
import com.asollaorta.amex.api.models.ItemDto;
import com.asollaorta.amex.api.models.Order;
import com.asollaorta.amex.api.models.OrderDto;
import com.asollaorta.amex.api.repositories.ItemRepository;
import com.asollaorta.amex.api.repositories.OrderRepository;
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


    public OrderDto makeOrder(Set<ItemDto> items) {

        if (items == null || items.isEmpty()) {
            throw new ApiException("Order must contain at least one item.",
                    HttpStatus.BAD_REQUEST, ZonedDateTime.now());
        }


        Set<ItemDto> validItems = items.stream()
                .filter(item -> itemRepository.existsByDescriptionIgnoreCase(item.getDescription()))
                .collect(Collectors.toSet());


        if (validItems.isEmpty()) {
            throw new ApiException("None of the items exist in the inventory.",
                    HttpStatus.BAD_REQUEST,
                    ZonedDateTime.now());
        }


        Set<Item> validItemsWithPrice = validItems.stream()
                .map(dto -> {
                    Item item = new Item();
                    item.setDescription(dto.getDescription());
                    item.setCost(itemRepository.findFirstByDescriptionIgnoreCase(dto.getDescription()).getCost());
                    item.setQuantity(dto.getQuantity());
                    return item;
                })
                .collect(Collectors.toSet());

        Order order = new Order();
        order.setFinalCost(this.finalCostCalculator(validItemsWithPrice));
        order.setItems(validItemsWithPrice);

        itemRepository.saveAll(validItemsWithPrice);

        return orderToDTO(orderRepository.save(order));
    }


    private BigDecimal finalCostCalculator(Set<Item> validItems) {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Item item : validItems) {
            BigDecimal itemTotal;

            if (item.getDescription().equalsIgnoreCase("Apple")) {
                int chargeableQuantity = (item.getQuantity() / 2) + (item.getQuantity() % 2);
                itemTotal = item.getCost().multiply(BigDecimal.valueOf(chargeableQuantity));

            } else if (item.getDescription().equalsIgnoreCase("Orange")) {
                int chargeableQuantity = (item.getQuantity() / 3) * 2 + (item.getQuantity() % 3);
                itemTotal = item.getCost().multiply(BigDecimal.valueOf(chargeableQuantity));
            } else {
                itemTotal = item.getCost().multiply(BigDecimal.valueOf(item.getQuantity()));
            }

            totalCost = totalCost.add(itemTotal);
        }

        return totalCost;
    }


    private OrderDto orderToDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto orderDTO = new OrderDto();
        orderDTO.setFinalCost(order.getFinalCost());
        orderDTO.setItems(convertItemsToDTO(order.getItems()));

        return orderDTO;
    }


    private Set<ItemDto> convertItemsToDTO(Set<Item> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .map(item -> {
                    ItemDto itemDTO = new ItemDto();
                    itemDTO.setCost(item.getCost());
                    itemDTO.setDescription(item.getDescription());
                    itemDTO.setQuantity(item.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toSet());
    }


}
