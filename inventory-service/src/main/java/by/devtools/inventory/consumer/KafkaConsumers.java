package by.devtools.inventory.consumer;

import by.devtools.domain.OrderDto;
import by.devtools.domain.Statuses;
import by.devtools.inventory.service.InventoryService;
import by.devtools.inventory.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumers {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void listenOrderEvents(String orderEvent) {
        log.info("Received order event in InventoryService: {}", orderEvent);
        OrderDto order = JsonUtil.fromJson(orderEvent, OrderDto.class);
        inventoryService.processInventory(order);
    }

    @KafkaListener(topics = "rollback-topic", groupId = "order-group")
    public void listenRollbackEvents(String rollbackEvent) {
        log.info("Received rollback event in InventoryService: {}", rollbackEvent);
        OrderDto order = JsonUtil.fromJson(rollbackEvent, OrderDto.class);
        if (Statuses.ACCEPTED.equals(order.getInventoryStatus())) {
            inventoryService.processRollback(order);
        }
    }
}
