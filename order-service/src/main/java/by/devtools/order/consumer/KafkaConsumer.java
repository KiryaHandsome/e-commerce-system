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
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderRepository orderRepository;
    //private final KafkaTemplate<Integer, String> kafkaTemplate;

    @KafkaListener(topics = "order-status-topic", groupId = "order-group")
    public void onOrderStatusEventReceived(String event) {
        StatusEvent statusEvent = JsonUtil.fromJson(event, StatusEvent.class);
        log.info("Received status event: {}", statusEvent);
        Order currentOrder = orderRepository.findById(statusEvent.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found. id = " + statusEvent.orderId()));
        switch (statusEvent.status()) {
            case Statuses.REJECTED -> {
                log.info("Process rejected status...");
                // stop global transaction
                // send message to rollback local transactions
                // set status CANCELLED
            }
            case Statuses.ACCEPTED -> {
                log.info("Processing accepted status...");
                // if order status is NEW just update status to WAITING in current order
                // if order status is WAITING - set status order CONFIRMED
            }
        }
    }
}
