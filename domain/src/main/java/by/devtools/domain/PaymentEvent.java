package by.devtools.domain;

public record PaymentEvent(
        Integer orderId,
        String paymentStatus
) {

}
