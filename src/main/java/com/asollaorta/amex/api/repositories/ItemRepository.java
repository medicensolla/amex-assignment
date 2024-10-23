package com.asollaorta.amex.api.repositories;

import com.asollaorta.amex.api.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {

    Item findByDescriptionIgnoreCase(String description);

    @Query("SELECT i.cost FROM Item i WHERE LOWER(i.description) = LOWER(:description)")
    BigDecimal getCostByDescriptionIgnoreCase(@Param("description") String description);

    boolean existsByDescriptionIgnoreCase(String description);
}
