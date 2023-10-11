package by.devtools.payment.consumer;


import by.devtools.domain.OrderDto;
import by.devtools.payment.service.PaymentService;
import by.devtools.payment.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void listenOrder(String orderEvent) {
        log.info("Payment service received order from order topic <{}>", orderEvent);
        OrderDto orderDto = JsonUtil.fromJson(orderEvent, OrderDto.class);
        paymentService.processOrder(orderDto.customerId(), orderDto.totalPrice());
    }

    @KafkaListener(topics = "rollback-topic", groupId = "order-group")
    public void listenRollback(String orderEvent) {
        OrderDto orderDto = JsonUtil.fromJson(orderEvent, OrderDto.class);
        paymentService.processRollback(orderDto.customerId(), orderDto.totalPrice());
    }
}
