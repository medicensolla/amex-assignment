package com.asollaorta.amex.api;

import com.asollaorta.amex.api.exceptions.ApiException;
import com.asollaorta.amex.api.models.Item;
import com.asollaorta.amex.api.models.ItemDto;
import com.asollaorta.amex.api.models.Order;
import com.asollaorta.amex.api.models.OrderDto;
import com.asollaorta.amex.api.repositories.ItemRepository;
import com.asollaorta.amex.api.repositories.OrderRepository;
import com.asollaorta.amex.api.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class OrderServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Item apple;
    private Item orange;
    private Set<ItemDto> mockItems;
    private Set<Item> itemSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        apple = new Item();
        apple.setDescription("Apple");
        apple.setCost(BigDecimal.valueOf(0.60));
        apple.setQuantity(5);

        orange = new Item();
        orange.setDescription("Orange");
        orange.setCost(BigDecimal.valueOf(0.25));
        orange.setQuantity(5);

        ItemDto appleDto = new ItemDto();
        appleDto.setDescription("Apple");
        appleDto.setQuantity(5);

        ItemDto orangeDto = new ItemDto();
        orangeDto.setDescription("Orange");
        orangeDto.setQuantity(5);

        mockItems = new HashSet<>();
        mockItems.add(appleDto);
        mockItems.add(orangeDto);

        itemSet = new HashSet<>();
        itemSet.add(apple);
        itemSet.add(orange);
    }

    @Test
    void testMakeOrder_Success_Task_One() {

        BigDecimal expectedFinalCost = BigDecimal.valueOf(1.10);

        apple.setQuantity(1);
        orange.setQuantity(2);

        when(itemRepository.existsByDescriptionIgnoreCase(apple.getDescription())).thenReturn(true);
        when(itemRepository.existsByDescriptionIgnoreCase(orange.getDescription())).thenReturn(true);
        when(itemRepository.findFirstByDescriptionIgnoreCase(anyString())).thenReturn(apple);
        when(itemRepository.findFirstByDescriptionIgnoreCase(anyString())).thenReturn(orange);

        Order savedOrder = new Order();
        savedOrder.setItems(itemSet);
        savedOrder.setFinalCost(orderService.finalCostCalculator(itemSet));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderDto order = orderService.makeOrder(mockItems);

        assertAll("Order Validation",
                () -> assertTrue(expectedFinalCost.compareTo(order.getFinalCost()) == 0, "Final cost should match"),
                () -> assertEquals(2, order.getItems().size(), "Order items count should match"),
                () -> assertTrue(order.getItems().stream()
                        .anyMatch(item -> item.getDescription().equals("Apple")), "Order should contain Apple"),
                () -> assertTrue(order.getItems().stream()
                        .anyMatch(item -> item.getDescription().equals("Orange")), "Order should contain Orange")
        );
    }

    @Test
    void testMakeOrder_Success_Task_Two() {

        BigDecimal expectedFinalCost = BigDecimal.valueOf(0.60);

        apple.setQuantity(2);

        when(itemRepository.existsByDescriptionIgnoreCase(apple.getDescription())).thenReturn(true);
        when(itemRepository.findFirstByDescriptionIgnoreCase(anyString())).thenReturn(apple);

        Order savedOrder = new Order();
        savedOrder.setItems(Set.of(apple));
        savedOrder.setFinalCost(orderService.finalCostCalculator(Set.of(apple)));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderDto order = orderService.makeOrder(mockItems);

        assertAll("Order Validation",
                () -> assertTrue(expectedFinalCost.compareTo(order.getFinalCost()) == 0, "Final cost should match"),
                () -> assertTrue(order.getItems().stream()
                        .anyMatch(item -> item.getDescription().equals("Apple")), "Order should contain Apple")
        );
    }


    @Test
    public void testMakeOrder_EmptyItems_ThrowsException() {
        Set<ItemDto> emptyItems = new HashSet<>();

        ApiException exception = assertThrows(ApiException.class, () -> {
            orderService.makeOrder(emptyItems);
        });

        assertEquals("Order must contain at least one item.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testMakeOrder_InvalidItems_ThrowsException() {
        when(itemRepository.existsByDescriptionIgnoreCase(apple.getDescription())).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> {
            orderService.makeOrder(mockItems);
        });

        assertEquals("None of the items exist in the inventory.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }
}
