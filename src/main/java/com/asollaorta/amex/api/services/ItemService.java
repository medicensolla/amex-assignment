package com.asollaorta.amex.api.services;

import com.asollaorta.amex.api.models.Item;
import com.asollaorta.amex.api.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
