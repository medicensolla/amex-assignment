package com.asollaorta.amex.api.repositories;

import com.asollaorta.amex.api.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {

    Item findByDescriptionIgnoreCase(String description);


    boolean existsByDescriptionIgnoreCase(String description);
}
