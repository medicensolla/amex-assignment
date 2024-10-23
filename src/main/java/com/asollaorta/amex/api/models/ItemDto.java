package com.asollaorta.amex.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private String description;
    private Integer quantity;
    private BigDecimal cost;

}
