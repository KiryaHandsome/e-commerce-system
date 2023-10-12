package by.devtools.domain;

public record InventoryEvent(
        Integer orderId,
        String inventoryStatus
) {

}
