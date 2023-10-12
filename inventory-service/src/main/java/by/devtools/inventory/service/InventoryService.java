package by.devtools.inventory.service;

import by.devtools.domain.OrderDto;

public interface InventoryService {

    void processInventory(OrderDto order);

    void processRollback(OrderDto order);
}
