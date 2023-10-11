package by.devtools.payment.service.impl;

import by.devtools.domain.OrderDto;
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
    public void processOrder(OrderDto order) {
        Balance balance = balanceRepository.findByCustomerId(order.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. customerId = " + order.customerId()));
        Double currentBalance = balance.getBalance();
        String orderStatus = Statuses.ACCEPTED;
        if (currentBalance < order.totalPrice()) {
            orderStatus = Statuses.REJECTED;
        } else {
            balance.setBalance(currentBalance - order.totalPrice());
            balanceRepository.save(balance);
        }
        String response = JsonUtil.toJson(new StatusEvent(order.id(), orderStatus));
        kafkaTemplate.send("order-status-topic", response);
    }

    /**
     * Returns money to user's balance.
     *
     * @param customerId user's id
     * @param totalPrice money to return
     */
    @Override
    public void processRollback(Integer customerId, Double totalPrice) {
        Balance balance = balanceRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("User not found. customerId = " + customerId));
        Double currentBalance = balance.getBalance();
        balance.setBalance(currentBalance + totalPrice);
        balanceRepository.save(balance);
    }
}
