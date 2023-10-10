package by.devtools.order.repository;

import by.devtools.order.model.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Integer id);
}
