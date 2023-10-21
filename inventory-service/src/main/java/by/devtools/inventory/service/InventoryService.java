package by.devtools.inventory.service;

import by.devtools.domain.OrderDto;
import by.devtools.domain.ResultEvent;

public interface InventoryService {

    ResultEvent processInventory(OrderDto order);

    void processRollback(OrderDto order);
}
