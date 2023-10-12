package by.devtools.inventory.exception;

import by.devtools.domain.BaseException;

public class InventoryNotFoundException extends BaseException {

    public InventoryNotFoundException(String message) {
        super(404, message);
    }
}
