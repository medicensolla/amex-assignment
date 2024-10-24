package com.asollaorta.amex.api.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class OrderDto {

    private Long orderId;
    private BigDecimal finalCost;
    private Set<ItemDto> items;
}
