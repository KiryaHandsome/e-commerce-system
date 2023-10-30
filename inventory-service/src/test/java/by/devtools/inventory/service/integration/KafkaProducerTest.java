package by.devtools.inventory.service.integration;

import by.devtools.domain.OrderDto;
import by.devtools.inventory.service.impl.KafkaProducer;
import by.devtools.inventory.util.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaProducerTest extends BaseIntegrationTest {

    private static final String TOPIC_NAME = "payment-result-topic";
    private static KafkaConsumer<Integer, String> consumer;

    @Autowired
    private KafkaProducer kafkaProducer;

    @BeforeAll
    static void setUp() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }

    @AfterAll
    static void tearDown() {
        consumer.close();
    }

    @Test
    void check_sendMessage_should_deliverMessageToTopic() {
        var message = new OrderDto(1, 1, 1, 1, 1.10, "", "", "");

        kafkaProducer.sendMessage(TOPIC_NAME, message);

        await()
                .atMost(10, SECONDS)
                .until(() -> {
                    ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(100));

                    if (records.isEmpty()) {
                        return false;
                    }

                    OrderDto actual = JsonUtil.fromJson(records.iterator().next().value(), OrderDto.class);

                    assertThat(records.count()).isEqualTo(1);
                    assertThat(actual).isEqualTo(message);
                    return true;
                });
    }

}