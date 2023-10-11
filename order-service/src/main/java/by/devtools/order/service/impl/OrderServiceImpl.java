package by.devtools.order.service.impl;

import by.devtools.domain.OrderDto;
import by.devtools.domain.Statuses;
import by.devtools.order.dto.OrderCreate;
import by.devtools.order.exception.OrderNotFoundException;
import by.devtools.order.mapper.OrderMapper;
import by.devtools.order.model.Order;
import by.devtools.order.repository.OrderRepository;
import by.devtools.order.service.OrderService;
import by.devtools.order.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderCreate request) {
        Order order = orderMapper.createToOrder(request);
        order.setStatus(Statuses.NEW);
        order = orderRepository.save(order);
        OrderDto orderData = orderMapper.orderToDto(order);
        kafkaTemplate.send("order-topic", JsonUtil.toJson(orderData));
        return orderData;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(Integer id) {
        return orderRepository.findById(id)
                .map(orderMapper::orderToDto)
                .orElseThrow(() -> new OrderNotFoundException("Order not found. id = " + id));
    }
}
