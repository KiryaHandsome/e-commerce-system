package by.devtools.domain;

public record OrderDto(
        Integer id,
        Integer productCount,
        Integer customerId,
        Integer productId,
        Double totalPrice
) {

}
