package by.devtools.inventory.service.impl;

import by.devtools.domain.OrderDto;
import by.devtools.domain.Statuses;
import by.devtools.inventory.exception.InventoryNotFoundException;
import by.devtools.inventory.model.Inventory;
import by.devtools.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    void check_processInventory_should_throw_InventoryNotFoundException() {
        int productId = 1;
        var order = new OrderDto(1, 1, 1, productId, 1.0, "", "", "");

        doReturn(Optional.empty())
                .when(inventoryRepository)
                .findByProductId(productId);

        Assertions.assertThrows(InventoryNotFoundException.class,
                () -> inventoryService.processInventory(order));

        verify(inventoryRepository).findByProductId(eq(productId));
    }

    @Test
    void check_processInventory_should_return_acceptedResult() {
        int productId = 1;
        int orderId = 2;
        var order = new OrderDto(orderId, 1, 1, productId, 1.0, "", "", "");
        var inventory = new Inventory(1, productId, 100);
        var expectedInventory = new Inventory(1, productId, 99);

        doReturn(Optional.of(inventory))
                .when(inventoryRepository)
                .findByProductId(productId);

        var actual = inventoryService.processInventory(order);

        assertThat(actual).isNotNull();
        assertThat(actual.orderId()).isEqualTo(orderId);
        assertThat(actual.status()).isEqualTo(Statuses.ACCEPTED);

        verify(inventoryRepository).save(eq(expectedInventory));
        verify(inventoryRepository).findByProductId(eq(productId));
    }

    @Test
    void check_processInventory_should_return_rejectedResult() {
        int productId = 1;
        int orderId = 2;
        var order = new OrderDto(orderId, 1, 1, productId, 1.0, "", "", "");
        var inventory = new Inventory(1, productId, 0);

        doReturn(Optional.of(inventory))
                .when(inventoryRepository)
                .findByProductId(productId);

        var actual = inventoryService.processInventory(order);

        assertThat(actual).isNotNull();
        assertThat(actual.orderId()).isEqualTo(orderId);
        assertThat(actual.status()).isEqualTo(Statuses.REJECTED);

        verify(inventoryRepository).findByProductId(eq(productId));
    }

    @Test
    void check_processRollback_should_call_findAndSave() {
        int productId = 1;
        int orderId = 2;
        var order = new OrderDto(orderId, 1, 1, productId, 1.0, "", "", "");
        var inventory = new Inventory(1, productId, 100);
        var expectedInventory = new Inventory(1, productId, 101);

        doReturn(Optional.of(inventory))
                .when(inventoryRepository)
                .findByProductId(productId);

        inventoryService.processRollback(order);

        verify(inventoryRepository).findByProductId(eq(productId));
        verify(inventoryRepository).save(eq(expectedInventory));
    }
}