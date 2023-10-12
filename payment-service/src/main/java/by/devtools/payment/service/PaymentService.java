package by.devtools.payment.service;


import by.devtools.domain.OrderDto;

public interface PaymentService {

    void processPayment(OrderDto order);

    void processRollback(OrderDto order);
}
