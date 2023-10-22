package by.devtools.order.service.impl;

import by.devtools.domain.OrderDto;
import by.devtools.domain.Statuses;
import by.devtools.order.dto.OrderCreate;
import by.devtools.order.exception.OrderNotFoundException;
import by.devtools.order.mapper.OrderMapper;
import by.devtools.order.model.Order;
import by.devtools.order.repository.OrderRepository;
import by.devtools.order.util.ServiceNames;
import by.devtools.order.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void check_createOrder_should_return_expectedResult() {
        OrderCreate createDto = TestData.getOrderCreate();
        Order orderToSave = TestData.getDefaultOrder();

        doReturn(TestData.getOrder())
                .when(orderMapper)
                .createToOrder(createDto);
        doReturn(orderToSave)
                .when(orderRepository)
                .save(orderToSave);
        doReturn(TestData.getOrderDto())
                .when(orderMapper)
                .orderToDto(orderToSave);

        OrderDto actual = orderService.createOrder(createDto);

        assertThat(actual).isEqualTo(TestData.getOrderDto());

        verify(orderMapper).createToOrder(createDto);
        verify(orderRepository).save(orderToSave);
        verify(orderMapper).orderToDto(orderToSave);
    }

    @Test
    void check_getOrder_should_return_expectedResult() {
        var expected = TestData.getOrderDto();

        doReturn(Optional.of(TestData.getOrder()))
                .when(orderRepository)
                .findById(TestData.ID);
        doReturn(expected)
                .when(orderMapper)
                .orderToDto(TestData.getOrder());

        var actual = orderService.getOrder(TestData.ID);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void check_getOrder_should_throw_OrderNotFoundException() {
        doReturn(Optional.empty())
                .when(orderRepository)
                .findById(TestData.ID);

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrder(TestData.ID));

        verify(orderRepository).findById(TestData.ID);
    }

    @Test
    void check_processResultEvent_should_throw_OrderNotFoundException() {
        doReturn(Optional.empty())
                .when(orderRepository)
                .findById(TestData.ID);

        assertThrows(OrderNotFoundException.class,
                () -> orderService.processResultEvent(TestData.ID, null, null));

        verify(orderRepository).findById(TestData.ID);
    }

    @Test
    void check_processResultEvent_forAcceptedInventory_should_saveExpectedOrder() {
        var expectedOrderToSave = TestData.getDefaultOrder();
        expectedOrderToSave.setInventoryStatus(Statuses.ACCEPTED);

        doReturn(Optional.of(TestData.getDefaultOrder()))
                .when(orderRepository)
                .findById(TestData.ID);

        orderService.processResultEvent(TestData.ID, Statuses.ACCEPTED, ServiceNames.INVENTORY);

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).findById(TestData.ID);
        verify(orderRepository).save(orderCapture.capture());

        assertThat(orderCapture.getValue()).isEqualTo(expectedOrderToSave);
    }

    @Test
    void check_processResultEvent_forRejectedInventory_should_saveExpectedOrder() {
        var expectedOrderToSave = TestData.getDefaultOrder();
        expectedOrderToSave.setOrderStatus(Statuses.CANCELLED);
        expectedOrderToSave.setInventoryStatus(Statuses.REJECTED);

        doReturn(Optional.of(TestData.getDefaultOrder()))
                .when(orderRepository)
                .findById(TestData.ID);

        orderService.processResultEvent(TestData.ID, Statuses.REJECTED, ServiceNames.INVENTORY);

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).findById(TestData.ID);
        verify(orderRepository).save(orderCapture.capture());

        assertThat(orderCapture.getValue()).isEqualTo(expectedOrderToSave);
    }

    @Test
    void check_processResultEvent_forAcceptedPayment_should_saveExpectedOrder() {
        var expectedOrderToSave = TestData.getDefaultOrder();
        expectedOrderToSave.setPaymentStatus(Statuses.ACCEPTED);

        doReturn(Optional.of(TestData.getDefaultOrder()))
                .when(orderRepository)
                .findById(TestData.ID);

        orderService.processResultEvent(TestData.ID, Statuses.ACCEPTED, ServiceNames.PAYMENT);

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).findById(TestData.ID);
        verify(orderRepository).save(orderCapture.capture());

        assertThat(orderCapture.getValue()).isEqualTo(expectedOrderToSave);
    }

    @Test
    void check_processResultEvent_forRejectedPayment_should_saveExpectedOrder() {
        var expectedOrderToSave = TestData.getDefaultOrder();
        expectedOrderToSave.setOrderStatus(Statuses.CANCELLED);
        expectedOrderToSave.setPaymentStatus(Statuses.REJECTED);

        doReturn(Optional.of(TestData.getDefaultOrder()))
                .when(orderRepository)
                .findById(TestData.ID);

        orderService.processResultEvent(TestData.ID, Statuses.REJECTED, ServiceNames.PAYMENT);

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).findById(TestData.ID);
        verify(orderRepository).save(orderCapture.capture());

        assertThat(orderCapture.getValue()).isEqualTo(expectedOrderToSave);
    }

    @Test
    void check_processResultEvent_forAcceptedPaymentWithAlreadyAcceptedInventory_should_beConfirmed() {
        var expectedOrderToSave = TestData.getDefaultOrder();
        expectedOrderToSave.setOrderStatus(Statuses.CONFIRMED);
        expectedOrderToSave.setPaymentStatus(Statuses.ACCEPTED);
        expectedOrderToSave.setInventoryStatus(Statuses.ACCEPTED);

        var order = TestData.getDefaultOrder();
        order.setInventoryStatus(Statuses.ACCEPTED);

        doReturn(Optional.of(order))
                .when(orderRepository)
                .findById(TestData.ID);

        orderService.processResultEvent(TestData.ID, Statuses.ACCEPTED, ServiceNames.PAYMENT);

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).findById(TestData.ID);
        verify(orderRepository).save(orderCapture.capture());

        assertThat(orderCapture.getValue()).isEqualTo(expectedOrderToSave);
    }

    @Test
    void check_processResultEvent_forAcceptedInventoryWithAlreadyAcceptedPayment_should_beConfirmed() {
        var expectedOrderToSave = TestData.getDefaultOrder();
        expectedOrderToSave.setOrderStatus(Statuses.CONFIRMED);
        expectedOrderToSave.setPaymentStatus(Statuses.ACCEPTED);
        expectedOrderToSave.setInventoryStatus(Statuses.ACCEPTED);

        var order = TestData.getDefaultOrder();
        order.setPaymentStatus(Statuses.ACCEPTED);

        doReturn(Optional.of(order))
                .when(orderRepository)
                .findById(TestData.ID);

        orderService.processResultEvent(TestData.ID, Statuses.ACCEPTED, ServiceNames.INVENTORY);

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).findById(TestData.ID);
        verify(orderRepository).save(orderCapture.capture());

        assertThat(orderCapture.getValue()).isEqualTo(expectedOrderToSave);
    }

    @Test
    void check_processResultEvent_forBothRejected_should_sendRollbackToKafka() {
        var expectedOrderToSave = TestData.getDefaultOrder();
        expectedOrderToSave.setOrderStatus(Statuses.CANCELLED);
        expectedOrderToSave.setPaymentStatus(Statuses.REJECTED);
        expectedOrderToSave.setInventoryStatus(Statuses.REJECTED);

        var order = TestData.getDefaultOrder();
        order.setOrderStatus(Statuses.CANCELLED);
        order.setInventoryStatus(Statuses.REJECTED);

        doReturn(Optional.of(order))
                .when(orderRepository)
                .findById(TestData.ID);

        orderService.processResultEvent(TestData.ID, Statuses.REJECTED, ServiceNames.PAYMENT);

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).findById(TestData.ID);
        verify(orderRepository).save(orderCapture.capture());
        verify(kafkaTemplate).send(eq("rollback-topic"), any());

        assertThat(orderCapture.getValue()).isEqualTo(expectedOrderToSave);
    }

    @Test
    void check_processResultEvent_forAcceptedInventory_should_sendRollbackToKafka() {
        var expectedOrderToSave = TestData.getDefaultOrder();
        expectedOrderToSave.setOrderStatus(Statuses.CANCELLED);
        expectedOrderToSave.setPaymentStatus(Statuses.REJECTED);
        expectedOrderToSave.setInventoryStatus(Statuses.ACCEPTED);

        var order = TestData.getDefaultOrder();
        order.setOrderStatus(Statuses.CANCELLED);
        order.setPaymentStatus(Statuses.REJECTED);

        doReturn(Optional.of(order))
                .when(orderRepository)
                .findById(TestData.ID);

        orderService.processResultEvent(TestData.ID, Statuses.ACCEPTED, ServiceNames.INVENTORY);

        ArgumentCaptor<Order> orderCapture = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).findById(TestData.ID);
        verify(orderRepository).save(orderCapture.capture());
        verify(kafkaTemplate).send(eq("rollback-topic"), any());

        assertThat(orderCapture.getValue()).isEqualTo(expectedOrderToSave);
    }
}