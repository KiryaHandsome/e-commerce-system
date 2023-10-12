package by.devtools.inventory.service.impl;

import by.devtools.domain.InventoryEvent;
import by.devtools.domain.OrderDto;
import by.devtools.domain.Statuses;
import by.devtools.inventory.exception.InventoryNotFoundException;
import by.devtools.inventory.model.Inventory;
import by.devtools.inventory.repository.InventoryRepository;
import by.devtools.inventory.service.InventoryService;
import by.devtools.inventory.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    /**
     * Reserve products at warehouse if enough.
     * Sends ACCEPTED response to order-status-topic
     * if there are enough products at warehouse,
     * otherwise - order with inventoryStatus = REJECTED
     *
     * @param order order to process
     */
    @Override
    public void processInventory(OrderDto order) {
        Inventory inventory = findInventoryByProductIdOrElseThrow(order.getProductId());
        Integer currentProductCount = inventory.getProductCount();
        String inventoryStatus = Statuses.ACCEPTED;
        if (currentProductCount < order.getProductCount()) {
            log.warn("Inventory service doesn't have {} products. InventoryStatus={}",
                    order.getProductCount(), Statuses.REJECTED);
            inventoryStatus = Statuses.REJECTED;
        } else {
            inventory.setProductCount(currentProductCount - order.getProductCount());
            inventoryRepository.save(inventory);
        }
        InventoryEvent inventoryEvent = new InventoryEvent(order.getId(), inventoryStatus);
        String response = JsonUtil.toJson(inventoryEvent);
        kafkaTemplate.send("inventory-topic", response);
    }

    @Override
    public void processRollback(OrderDto order) {
        Inventory inventory = findInventoryByProductIdOrElseThrow(order.getProductId());
        Integer newProductCount = inventory.getProductCount() + order.getProductCount();
        inventory.setProductCount(newProductCount);
        inventoryRepository.save(inventory);
    }

    private Inventory findInventoryByProductIdOrElseThrow(Integer productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(
                        () -> new InventoryNotFoundException(
                                "Inventory with such product id not found. productId = " + productId
                        )
                );
    }
}
