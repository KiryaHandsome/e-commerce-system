package by.devtools.order.consumer;

import by.devtools.domain.StatusEvent;
import by.devtools.order.exception.OrderNotFoundException;
import by.devtools.order.model.Order;
import by.devtools.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import by.devtools.domain.Statuses;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "order-status-topic", groupId = "order-group")
    public void onOrderStatusEventReceived(StatusEvent statusEvent) {
        log.info("Received status event: {}", statusEvent);
        Order currentOrder = orderRepository.findById(statusEvent.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found. id = " + statusEvent.orderId()));
        switch (statusEvent.status()) {
            case Statuses.REJECTED -> {
                log.info("Process rejected status...");
                // any reject will stop global transaction
            }
            case Statuses.ACCEPTED -> {
                log.info("Processing accepted status...");
                // check, if order status is paid - accept order
                // otherwise just update status in current order
            }
        }
    }
}
