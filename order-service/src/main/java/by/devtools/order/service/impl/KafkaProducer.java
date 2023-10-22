package by.devtools.order.service.impl;

import by.devtools.order.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public <T> void sendMessage(String topic, T payload) {
        CompletableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(topic, JsonUtil.toJson(payload));
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent message={} to topic={}", payload, topic);
            } else {
                log.warn("Error occurred when sending message={} to topic={}. Exception message: {} ",
                        payload, topic, ex.getMessage());
            }
        });
    }
}
