package by.devtools.order.service;


import by.devtools.domain.OrderDto;
import by.devtools.order.dto.OrderCreate;

public interface OrderService {

    OrderDto createOrder(OrderCreate request);

    OrderDto getOrder(int id);

    void processResultEvent(int id, String eventStatus, String senderName);
}
