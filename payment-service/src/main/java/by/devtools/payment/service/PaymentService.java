package by.devtools.payment.service;


import by.devtools.domain.OrderDto;
import by.devtools.domain.ResultEvent;

public interface PaymentService {

    ResultEvent processPayment(OrderDto order);

    void processRollback(OrderDto order);
}
