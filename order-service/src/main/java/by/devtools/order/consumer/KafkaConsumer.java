package by.devtools.order.consumer;

import by.devtools.domain.InventoryEvent;
import by.devtools.domain.OrderDto;
import by.devtools.domain.PaymentEvent;
import by.devtools.domain.Statuses;
import by.devtools.order.exception.OrderNotFoundException;
import by.devtools.order.mapper.OrderMapper;
import by.devtools.order.model.Order;
import by.devtools.order.repository.OrderRepository;
import by.devtools.order.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    @KafkaListener(topics = "inventory-topic", groupId = "order-group")
    public void listenInventoryEvents(String event) {
        log.info("Received InventoryEvent: {}", event);
        InventoryEvent inventoryEvent = JsonUtil.fromJson(event, InventoryEvent.class);
        Order order = orderRepository.findById(inventoryEvent.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found. id = " + inventoryEvent.orderId()));
        String newOrderStatus = null;
        switch (inventoryEvent.inventoryStatus()) {
            case Statuses.ACCEPTED -> {
                order.setInventoryStatus(Statuses.ACCEPTED);
                newOrderStatus = getNewOrderStatusOnAcceptedEvent(order.getOrderStatus(), order::getPaymentStatus);
            }
            case Statuses.REJECTED -> {
                newOrderStatus = Statuses.CANCELLED;
                order.setInventoryStatus(Statuses.REJECTED);
                if (isNotNew(order.getPaymentStatus())) { // only if payment transaction is completed
                    sendRollback(order);
                }
            }
        }
        order.setOrderStatus(newOrderStatus);
        orderRepository.save(order);
    }

    @KafkaListener(topics = "payment-topic", groupId = "order-group")
    public void listenPaymentEvents(String event) {
        log.info("Received PaymentEvent: {}", event);
        PaymentEvent paymentEvent = JsonUtil.fromJson(event, PaymentEvent.class);
        Order order = orderRepository.findById(paymentEvent.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found. id = " + paymentEvent.orderId()));
        String newOrderStatus = null;
        switch (paymentEvent.paymentStatus()) {
            case Statuses.ACCEPTED -> {
                order.setPaymentStatus(Statuses.ACCEPTED);
                newOrderStatus = getNewOrderStatusOnAcceptedEvent(order.getOrderStatus(), order::getInventoryStatus);
            }
            case Statuses.REJECTED -> {
                newOrderStatus = Statuses.CANCELLED;
                order.setPaymentStatus(Statuses.REJECTED);
                if (isNotNew(order.getPaymentStatus())) { // only if inventory transaction is completed
                    sendRollback(order);
                }
            }
        }
        order.setOrderStatus(newOrderStatus);
        orderRepository.save(order);
    }

    private String getNewOrderStatusOnAcceptedEvent(String currentStatus, Supplier<String> otherServiceStatus) {
        if (Statuses.NEW.equals(currentStatus)) {
            return Statuses.WAITING;
        } else if (Statuses.ACCEPTED.equals(otherServiceStatus.get())) {
            return Statuses.CONFIRMED;
        } else {
            return Statuses.CONFIRMED;
        }
    }

    private boolean isNotNew(String status) {
        return !Statuses.NEW.equals(status);
    }

    private void sendRollback(Order order) {
        OrderDto data = orderMapper.orderToDto(order);
        kafkaTemplate.send("rollback-topic", JsonUtil.toJson(data));
    }

}
