package by.devtools.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderCreate(
        @Min(1)
        @Max(20)
        Integer amount,
        //@NotNull
        //Integer customerId, todo do i need this?
        @NotNull
        Integer productId,
        @NotNull
        Double totalPrice
) {

}
