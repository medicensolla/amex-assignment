package com.asollaorta.amex.api;

import com.asollaorta.amex.api.exceptions.ApiException;
import com.asollaorta.amex.api.models.Item;
import com.asollaorta.amex.api.models.Order;
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
import static org.mockito.Mockito.when;

public class OrderServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;  // Class under test

    private Item mockItem;
    private Set<Item> mockItems;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setCost(BigDecimal.valueOf(10.50));
        mockItem.setQuantity(2);

        mockItems = new HashSet<>();
        mockItems.add(mockItem);
    }

    @Test
   public void testMakeOrder_Success() {
        when(itemRepository.existsById(mockItem.getId())).thenReturn(true);

        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setItems(mockItems);
        mockOrder.setFinalCost(BigDecimal.valueOf(21.00));

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        Order order = orderService.makeOrder(mockItems);



        assertEquals(1L, order.getId());
        assertEquals(BigDecimal.valueOf(21.00), order.getFinalCost());
        assertEquals(1, order.getItems().size());
    }

    @Test
    void testMakeOrder_Success_Task_One() {

        BigDecimal expectedFinalCost = BigDecimal.valueOf(4.25);

        Item apple = new Item();
        apple.setId(1L);
        apple.setDescription("Apple");
        apple.setCost(BigDecimal.valueOf(0.60));
        apple.setQuantity(5);

        Item orange = new Item();
        orange.setId(2L);
        orange.setCost(BigDecimal.valueOf(0.25));
        orange.setQuantity(5);

        mockItems = new HashSet<>();
        mockItems.add(apple);
        mockItems.add(orange);

        when(itemRepository.existsById(apple.getId())).thenReturn(true);
        when(itemRepository.existsById(orange.getId())).thenReturn(true);


        Order mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setItems(mockItems);
        mockOrder.setFinalCost(expectedFinalCost);

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        Order order = orderService.makeOrder(mockItems);

        assertAll("Order Validation",
                () -> assertEquals(1L, order.getId(), "Order ID match"),
                () -> assertEquals(expectedFinalCost, order.getFinalCost(), "Final cost match"),
                () -> assertEquals(2, order.getItems().size(), "Order items count match")
        );
    }

    @Test
    public void testMakeOrder_EmptyItems_ThrowsException() {
        Set<Item> emptyItems = new HashSet<>();

        ApiException exception = assertThrows(ApiException.class, () -> {
            orderService.makeOrder(emptyItems);
        });

        assertEquals("Order must contain at least one item.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testMakeOrder_InvalidItems_ThrowsException() {
        when(itemRepository.existsById(mockItem.getId())).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> {
            orderService.makeOrder(mockItems);
        });

        assertEquals("None of the items exist in the inventory.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }
}
