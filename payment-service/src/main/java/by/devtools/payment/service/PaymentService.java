package by.devtools.payment.service;


public interface PaymentService {

    void processOrder(Integer customerId, Double totalPrice);

    void processRollback(Integer customerId, Double totalPrice);
}
