package by.devtools.inventory.consumer;

import by.devtools.domain.OrderDto;
import by.devtools.domain.ResultEvent;
import by.devtools.domain.Statuses;
import by.devtools.inventory.service.InventoryService;
import by.devtools.inventory.service.impl.KafkaProducer;
import by.devtools.inventory.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumers {

    private final KafkaProducer kafkaProducer;
    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-created-topic", groupId = "inventory-service")
    public void listenOrderEvents(String event) {
        log.info("Received order event in InventoryService: {}", event);
        OrderDto order = JsonUtil.fromJson(event, OrderDto.class);
        ResultEvent resultEvent = inventoryService.processInventory(order);
        kafkaProducer.sendMessage("inventory-result-topic", resultEvent);
    }

    @KafkaListener(topics = "rollback-topic", groupId = "inventory-service")
    public void listenRollbackEvents(String event) {
        log.info("Received rollback event in InventoryService: {}", event);
        OrderDto order = JsonUtil.fromJson(event, OrderDto.class);
        if (Statuses.ACCEPTED.equals(order.getInventoryStatus())) {
            inventoryService.processRollback(order);
        }
    }
}
