package by.devtools.order.integration.consumer;

import by.devtools.domain.ResultEvent;
import by.devtools.domain.Statuses;
import by.devtools.order.controller.OrderController;
import by.devtools.order.integration.BaseIntegrationTest;
import by.devtools.order.model.Order;
import by.devtools.order.repository.OrderRepository;
import by.devtools.order.util.JsonUtil;
import by.devtools.order.util.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaConsumersIT extends BaseIntegrationTest {


    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderController orderController;

    @Test
    void check_listenInventoryEvents_should_updateInventoryStatusInDatabaseToAccepted() {
        Integer orderId = orderController.createOrder(TestData.getOrderCreate())
                .getBody()
                .getId();

        var message = new ResultEvent(orderId, Statuses.ACCEPTED);

        kafkaTemplate.send("inventory-result-topic", JsonUtil.toJson(message));

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    Optional<Order> order = orderRepository.findById(orderId);
                    assertThat(order).isPresent();
                    assertThat(order.get().getOrderStatus()).isEqualTo(Statuses.IN_PROCESS);
                    assertThat(order.get().getPaymentStatus()).isEqualTo(Statuses.IN_PROCESS);
                    assertThat(order.get().getInventoryStatus()).isEqualTo(Statuses.ACCEPTED);
                });
    }

    @Test
    void check_listenPaymentEvents_should_updatePaymentStatusInDatabaseToAccepted() {
        Integer orderId = orderController.createOrder(TestData.getOrderCreate())
                .getBody()
                .getId();

        var message = new ResultEvent(orderId, Statuses.ACCEPTED);

        kafkaTemplate.send("payment-result-topic", JsonUtil.toJson(message));

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    Optional<Order> order = orderRepository.findById(orderId);
                    assertThat(order).isPresent();
                    assertThat(order.get().getOrderStatus()).isEqualTo(Statuses.IN_PROCESS);
                    assertThat(order.get().getPaymentStatus()).isEqualTo(Statuses.ACCEPTED);
                    assertThat(order.get().getInventoryStatus()).isEqualTo(Statuses.IN_PROCESS);
                });
    }

    @Test
    void check_listenPaymentEvents_should_cancelOrder() {
        Integer orderId = orderController.createOrder(TestData.getOrderCreate())
                .getBody()
                .getId();

        var message = new ResultEvent(orderId, Statuses.REJECTED);

        kafkaTemplate.send("payment-result-topic", JsonUtil.toJson(message));

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(15, SECONDS)
                .untilAsserted(() -> {
                    Optional<Order> order = orderRepository.findById(orderId);
                    assertThat(order).isPresent();
                    assertThat(order.get().getPaymentStatus()).isEqualTo(Statuses.REJECTED);
                    assertThat(order.get().getInventoryStatus()).isEqualTo(Statuses.IN_PROCESS);
                    assertThat(order.get().getOrderStatus()).isEqualTo(Statuses.CANCELLED);
                });
    }

    @Test
    void check_listenInventoryEvents_should_cancelOrder() {
        Integer orderId = orderController.createOrder(TestData.getOrderCreate())
                .getBody()
                .getId();

        var message = new ResultEvent(orderId, Statuses.REJECTED);

        kafkaTemplate.send("inventory-result-topic", JsonUtil.toJson(message));

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    Optional<Order> order = orderRepository.findById(orderId);
                    assertThat(order).isPresent();
                    assertThat(order.get().getPaymentStatus()).isEqualTo(Statuses.IN_PROCESS);
                    assertThat(order.get().getInventoryStatus()).isEqualTo(Statuses.REJECTED);
                    assertThat(order.get().getOrderStatus()).isEqualTo(Statuses.CANCELLED);
                });
    }

    @Test
    void check_listenBothEvents_should_cancelOrder() {
        Integer orderId = orderController.createOrder(TestData.getOrderCreate())
                .getBody()
                .getId();

        var message = new ResultEvent(orderId, Statuses.REJECTED);

        kafkaTemplate.send("inventory-result-topic", JsonUtil.toJson(message));
        kafkaTemplate.send("payment-result-topic", JsonUtil.toJson(message));

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    Optional<Order> order = orderRepository.findById(orderId);
                    assertThat(order).isPresent();
                    assertThat(order.get().getPaymentStatus()).isEqualTo(Statuses.REJECTED);
                    assertThat(order.get().getInventoryStatus()).isEqualTo(Statuses.REJECTED);
                    assertThat(order.get().getOrderStatus()).isEqualTo(Statuses.CANCELLED);
                });
    }

    @Test
    void check_listenBothEvents_should_confirmOrder() {
        Integer orderId = orderController.createOrder(TestData.getOrderCreate())
                .getBody()
                .getId();

        var message = new ResultEvent(orderId, Statuses.ACCEPTED);

        kafkaTemplate.send("inventory-result-topic", JsonUtil.toJson(message));
        kafkaTemplate.send("payment-result-topic", JsonUtil.toJson(message));

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(15, SECONDS)
                .untilAsserted(() -> {
                    Optional<Order> order = orderRepository.findById(orderId);
                    assertThat(order).isPresent();
                    assertThat(order.get().getPaymentStatus()).isEqualTo(Statuses.ACCEPTED);
                    assertThat(order.get().getInventoryStatus()).isEqualTo(Statuses.ACCEPTED);
                    assertThat(order.get().getOrderStatus()).isEqualTo(Statuses.CONFIRMED);
                });
    }
}