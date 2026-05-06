package io.swagger.petstore.controller;

import io.swagger.petstore.model.Order;
import io.swagger.petstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final OrderService orderService;

    public StoreController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> getInventory() {
        return orderService.inventory();
    }

    @PostMapping(value = "/order",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Order placeOrder(@Valid @RequestBody Order order) {
        return orderService.place(order);
    }

    @GetMapping(value = "/order/{orderId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Order getOrderById(@PathVariable("orderId") Long orderId) {
        return orderService.getById(orderId);
    }

    @DeleteMapping("/order/{orderId}")
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.delete(orderId);
    }
}
