package by.devtools.payment.service.impl;

import by.devtools.domain.Statuses;
import by.devtools.payment.exception.CustomerNotFoundException;
import by.devtools.payment.model.Balance;
import by.devtools.payment.repository.BalanceRepository;
import by.devtools.payment.service.PaymentService;
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
     * @param customerId id of user to process balance
     * @param totalPrice total price of order
     */
    @Override
    public void processOrder(Integer customerId, Double totalPrice) {
        Balance balance = balanceRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found. customerId = " + customerId));
        Double currentBalance = balance.getBalance();
        String orderStatus = Statuses.ACCEPTED;
        if (currentBalance < totalPrice) {
            orderStatus = Statuses.REJECTED;
        } else {
            balance.setBalance(currentBalance - totalPrice);
            balanceRepository.save(balance);
        }
        kafkaTemplate.send("order-status-topic", orderStatus);
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
