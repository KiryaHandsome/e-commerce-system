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
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
        order.setOrderStatus(Statuses.IN_PROCESS);
        order.setPaymentStatus(Statuses.IN_PROCESS);
        order.setInventoryStatus(Statuses.IN_PROCESS);
        order = orderRepository.save(order);
        return orderMapper.orderToDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrder(int id) {
        return orderRepository.findById(id)
                .map(orderMapper::orderToDto)
                .orElseThrow(() -> new OrderNotFoundException("Order not found. id = " + id));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 10)
    public void processResultEvent(int id, String eventStatus, String senderName) {
        Order currentOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found. id = " + id));
        if (Statuses.ACCEPTED.equals(eventStatus)) {
            processAcceptedEvent(currentOrder, senderName);
        } else if (Statuses.REJECTED.equals(eventStatus)) {
            processRejectedEvent(currentOrder, senderName);
        }
        orderRepository.save(currentOrder);
    }

    private void processAcceptedEvent(Order order, String senderName) {
        String orderStatus = order.getOrderStatus();
        updateServiceStatusByName(senderName, order, Statuses.ACCEPTED);
        if (Statuses.IN_PROCESS.equals(orderStatus) && allServicesStatusesAccepted(order)) {
            order.setOrderStatus(Statuses.CONFIRMED);
        } else if (Statuses.CANCELLED.equals(orderStatus) && allEventsCompleted(order)) {
            sendRollback(order);
        }
    }

    private void processRejectedEvent(Order order, String senderName) {
        order.setOrderStatus(Statuses.CANCELLED);
        updateServiceStatusByName(senderName, order, Statuses.REJECTED);
        if (allEventsCompleted(order)) {
            sendRollback(order);
        }
    }

    private void updateServiceStatusByName(String senderName, Order order, String newStatus) {
        if ("inventory".equals(senderName)) {
            order.setInventoryStatus(newStatus);
        } else if ("payment".equals(senderName)) {
            order.setPaymentStatus(newStatus);
        }
    }

    private boolean allServicesStatusesAccepted(Order order) {
        return Statuses.ACCEPTED.equals(order.getInventoryStatus())
                && Statuses.ACCEPTED.equals(order.getPaymentStatus());
    }

    private void sendRollback(Order order) {
        OrderDto orderDto = orderMapper.orderToDto(order);
        kafkaTemplate.send("rollback-topic", JsonUtil.toJson(orderDto));
    }

    /**
     * Check if all transactions(events) completed(don't have status {@link Statuses#IN_PROCESS})
     * then return true
     */
    private boolean allEventsCompleted(Order order) {
        return !Statuses.IN_PROCESS.equals(order.getPaymentStatus())
                && !Statuses.IN_PROCESS.equals(order.getInventoryStatus());
    }
}
