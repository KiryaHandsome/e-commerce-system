package by.devtools.inventory.service.impl;

import by.devtools.domain.OrderDto;
import by.devtools.domain.ResultEvent;
import by.devtools.domain.Statuses;
import by.devtools.inventory.exception.InventoryNotFoundException;
import by.devtools.inventory.model.Inventory;
import by.devtools.inventory.repository.InventoryRepository;
import by.devtools.inventory.service.InventoryService;
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

    /**
     * Reserve products at warehouse if enough.
     *
     * @param order order to process
     * @return result event with {@link Statuses#ACCEPTED } status if there is enough products
     * otherwise {@link Statuses#REJECTED }
     */
    @Override
    public ResultEvent processInventory(OrderDto order) {
        Inventory inventory = findInventoryByProductId(order.getProductId());
        Integer currentProductCount = inventory.getProductCount();
        String inventoryStatus = Statuses.ACCEPTED;
        if (currentProductCount < order.getProductCount()) {
            log.debug("Inventory service doesn't have {} products. InventoryStatus={}",
                    order.getProductCount(), Statuses.REJECTED);
            inventoryStatus = Statuses.REJECTED;
        } else {
            inventory.setProductCount(currentProductCount - order.getProductCount());
            inventoryRepository.save(inventory);
        }
        return new ResultEvent(order.getId(), inventoryStatus);

    }

    @Override
    public void processRollback(OrderDto order) {
        Inventory inventory = findInventoryByProductId(order.getProductId());
        Integer newProductCount = inventory.getProductCount() + order.getProductCount();
        inventory.setProductCount(newProductCount);
        inventoryRepository.save(inventory);
    }

    private Inventory findInventoryByProductId(Integer productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(
                        () -> new InventoryNotFoundException(
                                "Inventory with such product id not found. productId = " + productId
                        )
                );
    }
}
