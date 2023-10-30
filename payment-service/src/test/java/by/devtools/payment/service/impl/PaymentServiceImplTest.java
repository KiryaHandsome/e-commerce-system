package by.devtools.payment.service.impl;

import by.devtools.domain.OrderDto;
import by.devtools.domain.Statuses;
import by.devtools.payment.exception.CustomerNotFoundException;
import by.devtools.payment.model.Balance;
import by.devtools.payment.repository.BalanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void check_processPayment_should_throw_InventoryNotFoundException() {
        int customerId = 1;
        var order = new OrderDto(1, 1, customerId, 1, 1.0, "", "", "");

        doReturn(Optional.empty())
                .when(balanceRepository)
                .findByCustomerId(customerId);

        assertThrows(CustomerNotFoundException.class,
                () -> paymentService.processPayment(order));

        verify(balanceRepository).findByCustomerId(eq(customerId));
    }

    @Test
    void check_processPayment_should_return_acceptedResult() {
        int customerId = 1;
        int orderId = 2;
        var order = new OrderDto(orderId, 1, customerId, 1, 1.0, "", "", "");
        var balance = new Balance(1, customerId, 100.00);
        var expectedBalance = new Balance(1, customerId, 99.00);

        doReturn(Optional.of(balance))
                .when(balanceRepository)
                .findByCustomerId(customerId);

        var actual = paymentService.processPayment(order);

        assertThat(actual).isNotNull();
        assertThat(actual.orderId()).isEqualTo(orderId);
        assertThat(actual.status()).isEqualTo(Statuses.ACCEPTED);

        verify(balanceRepository).save(eq(expectedBalance));
        verify(balanceRepository).findByCustomerId(eq(customerId));
    }

    @Test
    void check_processInventory_should_return_rejectedResult() {
        int customerId = 1;
        int orderId = 2;
        var order = new OrderDto(orderId, 1, 1, customerId, 1.0, "", "", "");
        var balance = new Balance(1, customerId, 0.0);

        doReturn(Optional.of(balance))
                .when(balanceRepository)
                .findByCustomerId(customerId);

        var actual = paymentService.processPayment(order);

        assertThat(actual).isNotNull();
        assertThat(actual.orderId()).isEqualTo(orderId);
        assertThat(actual.status()).isEqualTo(Statuses.REJECTED);

        verify(balanceRepository).findByCustomerId(eq(customerId));
    }

    @Test
    void check_processRollback_should_call_findAndSave() {
        int customerId = 1;
        int orderId = 2;
        var order = new OrderDto(orderId, 1, customerId, 1, 1.0, "", "", "");
        var balance = new Balance(1, customerId, 100.0);
        var expectedBalance = new Balance(1, customerId, 101.0);

        doReturn(Optional.of(balance))
                .when(balanceRepository)
                .findByCustomerId(customerId);

        paymentService.processRollback(order);

        verify(balanceRepository).findByCustomerId(eq(customerId));
        verify(balanceRepository).save(eq(expectedBalance));
    }
}