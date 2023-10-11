package by.devtools.order.consumer;

import by.devtools.domain.StatusEvent;
import by.devtools.domain.Statuses;
import by.devtools.order.exception.OrderNotFoundException;
import by.devtools.order.model.Order;
import by.devtools.order.repository.OrderRepository;
import by.devtools.order.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    @KafkaListener(topics = "order-status-topic", groupId = "order-group")
    public void onOrderStatusEventReceived(String event) {
        StatusEvent statusEvent = JsonUtil.fromJson(event, StatusEvent.class);
        log.info("Received status event: {}", statusEvent);
        Order currentOrder = orderRepository.findById(statusEvent.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found. id = " + statusEvent.orderId()));
        String newStatus = null;
        switch (statusEvent.status()) {
            case Statuses.REJECTED -> {
                log.info("Status event = rejected");
                kafkaTemplate.send("rollback-topic", JsonUtil.toJson(currentOrder));
                newStatus = Statuses.CANCELLED;
            }
            case Statuses.ACCEPTED -> {
                log.info("Status event = accepted");
                if (Statuses.NEW.equals(currentOrder.getStatus())) {
                    newStatus = Statuses.WAITING;
                } else if (Statuses.WAITING.equals(currentOrder.getStatus())) {
                    newStatus = Statuses.CONFIRMED;
                }
            }
        }
        currentOrder.setStatus(newStatus);
        log.info("Updating order with id={} newStatus={}", currentOrder.getId(), newStatus);
        orderRepository.save(currentOrder);
    }
}
