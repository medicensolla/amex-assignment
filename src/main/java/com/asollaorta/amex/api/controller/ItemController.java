package com.asollaorta.amex.api.controller;

import com.asollaorta.amex.api.models.Item;
import com.asollaorta.amex.api.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/")
    public List<Item> getAllItems() {

        return itemService.getAllItems();
    }
}
