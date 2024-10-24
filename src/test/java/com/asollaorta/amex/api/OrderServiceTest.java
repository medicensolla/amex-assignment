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
import java.util.*;

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
    private List<Item> itemSet;

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

        itemSet = new ArrayList<>();
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
        savedOrder.setItems(List.of(apple));
        savedOrder.setFinalCost(orderService.finalCostCalculator(List.of(apple)));

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


    @Test
    void testGetAllOrders_Success() {

        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findAll()).thenReturn(orders);


        List<OrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllOrders_NoOrdersFound() {

        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        ApiException exception = assertThrows(ApiException.class, () -> {
            orderService.getAllOrders();
        });

        assertEquals("No orders found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void testGetOrderById_Success() {
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setFinalCost(BigDecimal.valueOf(100.50));
        mockOrder.setItems(List.of(apple, orange));


        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        OrderDto result = orderService.getOrderById(orderId);

        assertAll("GetOrder Validation",
                () -> assertNotNull(result, "Order should not be null"),
                () -> assertEquals(orderId, result.getOrderId(), "Order ID should match"),
                () -> assertEquals(BigDecimal.valueOf(100.50), result.getFinalCost(), "Final cost should match"),
                () -> assertFalse(result.getItems().isEmpty(), "Order items should not be empty"),
                () -> assertTrue(
                        result.getItems().stream()
                                .anyMatch(item -> item.getDescription().equals("Apple")),
                        "Order should contain an item with description 'Apple'"
                )
        );
    }

    @Test
    void testGetOrderById_NotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            orderService.getOrderById(orderId);
        });

        assertAll("Exception Validation",
                () -> assertEquals(String.format("Order with ID %d not found.", orderId), exception.getMessage(),
                        "Exception message should match"),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus(),
                        "HTTP status should be NOT_FOUND")
        );
    }
}
