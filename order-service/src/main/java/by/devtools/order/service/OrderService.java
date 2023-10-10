package by.devtools.order.service;


import by.devtools.domain.OrderDto;
import by.devtools.order.dto.OrderCreate;
import by.devtools.order.dto.OrderCreatedResponse;

public interface OrderService {

    OrderCreatedResponse createOrder(OrderCreate request);

    OrderDto getOrder(Integer id);
}
