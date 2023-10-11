package by.devtools.payment.service;


import by.devtools.domain.OrderDto;

public interface PaymentService {

    void processOrder(OrderDto order);

    void processRollback(Integer customerId, Double totalPrice);
}
