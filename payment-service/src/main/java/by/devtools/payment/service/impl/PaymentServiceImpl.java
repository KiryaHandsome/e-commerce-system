package by.devtools.payment.service.impl;

import by.devtools.domain.OrderDto;
import by.devtools.domain.ResultEvent;
import by.devtools.domain.Statuses;
import by.devtools.payment.exception.CustomerNotFoundException;
import by.devtools.payment.model.Balance;
import by.devtools.payment.repository.BalanceRepository;
import by.devtools.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BalanceRepository balanceRepository;

    /**
     * Reserves money at user's balance.
     *
     * @param order order to process
     * @return object with status {@link Statuses#ACCEPTED } if enough money
     * {@link Statuses#REJECTED } otherwise
     */
    @Override
    public ResultEvent processPayment(OrderDto order) {
        Balance balance = findBalanceByCustomerId(order.getCustomerId());
        Double currentBalance = balance.getBalance();
        String paymentStatus = Statuses.ACCEPTED;
        if (currentBalance < order.getTotalPrice()) {
            paymentStatus = Statuses.REJECTED;
        } else {
            balance.setBalance(currentBalance - order.getTotalPrice());
            balanceRepository.save(balance);
        }
        return new ResultEvent(order.getId(), paymentStatus);
    }

    /**
     * Returns money to user's balance.
     *
     * @param order order which is rollback for
     */
    @Override
    public void processRollback(OrderDto order) {
        Balance balance = findBalanceByCustomerId(order.getCustomerId());
        Double currentBalance = balance.getBalance();
        balance.setBalance(currentBalance + order.getTotalPrice());
        balanceRepository.save(balance);
    }

    private Balance findBalanceByCustomerId(int customerId) {
        return balanceRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "User not found. customerId = " + customerId)
                );
    }
}
