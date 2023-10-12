package by.devtools.payment.service.impl;

import by.devtools.domain.OrderDto;
import by.devtools.domain.PaymentEvent;
import by.devtools.domain.StatusEvent;
import by.devtools.domain.Statuses;
import by.devtools.payment.exception.CustomerNotFoundException;
import by.devtools.payment.model.Balance;
import by.devtools.payment.repository.BalanceRepository;
import by.devtools.payment.service.PaymentService;
import by.devtools.payment.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BalanceRepository balanceRepository;
    private final KafkaTemplate<Integer, String> kafkaTemplate;

    /**
     * Reserves money at user's balance.
     * Sends success/failure response to order-status-topic
     *
     * @param order order to process
     */
    @Override
    public void processPayment(OrderDto order) {
        Balance balance = balanceRepository.findByCustomerId(order.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. customerId = " + order.getCustomerId()));
        Double currentBalance = balance.getBalance();
        String paymentStatus = Statuses.ACCEPTED;
        if (currentBalance < order.getTotalPrice()) {
            paymentStatus = Statuses.REJECTED;
        } else {
            balance.setBalance(currentBalance - order.getTotalPrice());
            balanceRepository.save(balance);
        }
        PaymentEvent response = new PaymentEvent(order.getId(), paymentStatus);
        kafkaTemplate.send("payment-topic", JsonUtil.toJson(response));
    }

    /**
     * Returns money to user's balance.
     *
     * @param order order which is rollback for
     */
    @Override
    public void processRollback(OrderDto order) {
        Balance balance = balanceRepository.findByCustomerId(order.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("User not found. customerId = " + order.getCustomerId()));
        Double currentBalance = balance.getBalance();
        balance.setBalance(currentBalance + order.getTotalPrice());
        balanceRepository.save(balance);
    }
}
