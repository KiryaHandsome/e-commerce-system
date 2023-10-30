package by.devtools.payment.consumer;


import by.devtools.domain.OrderDto;
import by.devtools.domain.ResultEvent;
import by.devtools.domain.Statuses;
import by.devtools.payment.service.PaymentService;
import by.devtools.payment.service.impl.KafkaProducer;
import by.devtools.payment.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumers {

    private final PaymentService paymentService;
    private final KafkaProducer kafkaProducer;

    @KafkaListener(topics = "order-created-topic", groupId = "payment-service")
    public void listenOrder(String orderEvent) {
        log.info("Received order event in PaymentService: {}", orderEvent);
        OrderDto order = JsonUtil.fromJson(orderEvent, OrderDto.class);
        ResultEvent resultEvent = paymentService.processPayment(order);
        kafkaProducer.sendMessage("payment-result-topic", resultEvent);
    }

    @KafkaListener(topics = "rollback-topic", groupId = "payment-service")
    public void listenRollback(String rollbackEvent) {
        log.info("Received rollback event in PaymentService: {}", rollbackEvent);
        OrderDto order = JsonUtil.fromJson(rollbackEvent, OrderDto.class);
        if (Statuses.ACCEPTED.equals(order.getPaymentStatus())) {
            paymentService.processRollback(order);
        }
    }
}
