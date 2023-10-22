package by.devtools.order.consumer;

import by.devtools.domain.ResultEvent;
import by.devtools.order.service.OrderService;
import by.devtools.order.util.JsonUtil;
import by.devtools.order.util.ServiceNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "inventory-result-topic", groupId = "order-service")
    public void listenInventoryEvents(String event) {
        log.info("Received InventoryEvent: {}", event);
        ResultEvent inventoryEvent = JsonUtil.fromJson(event, ResultEvent.class);
        orderService.processResultEvent(
                inventoryEvent.orderId(),
                inventoryEvent.status(),
                ServiceNames.INVENTORY
        );
    }

    @KafkaListener(topics = "payment-result-topic", groupId = "order-service")
    public void listenPaymentEvents(String event) {
        log.info("Received PaymentEvent: {}", event);
        ResultEvent inventoryEvent = JsonUtil.fromJson(event, ResultEvent.class);
        orderService.processResultEvent(
                inventoryEvent.orderId(),
                inventoryEvent.status(),
                ServiceNames.PAYMENT
        );
    }
}
