package io.swagger.petstore.service;

import io.swagger.petstore.exception.NotFoundException;
import io.swagger.petstore.model.Order;
import io.swagger.petstore.model.Pet;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    private final ConcurrentMap<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(100);
    private final PetService petService;

    public OrderService(PetService petService) {
        this.petService = petService;
    }

    public Order place(Order order) {
        if (order.getId() == null || order.getId() == 0L) {
            order.setId(idSeq.incrementAndGet());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public Order getById(long id) {
        Order o = orders.get(id);
        if (o == null) throw new NotFoundException("Order not found");
        return o;
    }

    public void delete(long id) {
        if (orders.remove(id) == null) {
            throw new NotFoundException("Order not found");
        }
    }

    /** Map of pet-status -> count of pets currently in that status. */
    public Map<String, Integer> inventory() {
        Map<String, Integer> map = new HashMap<>();
        for (Pet p : petService.all()) {
            if (p.getStatus() == null) continue;
            map.merge(p.getStatus().getValue(), 1, Integer::sum);
        }
        return map;
    }
}
