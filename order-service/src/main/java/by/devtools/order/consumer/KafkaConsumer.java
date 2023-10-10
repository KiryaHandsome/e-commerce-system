package by.devtools.order.consumer;

import by.devtools.domain.StatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import by.devtools.domain.StatusEvents;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    // todo inject order repository

    @KafkaListener(topics = "order-status-topic", groupId = "order-group")
    public void onOrderStatusEventReceived(StatusEvent statusEvent) {
        log.info("Received status event: {}", statusEvent);
        switch (statusEvent.status()) {
            case StatusEvents.REJECTED -> {
                log.info("Process rejected status...");
                // any reject will stop global transaction
            }
            case StatusEvents.ACCEPTED -> {
                log.info("Processing accepted status...");
                // check, if order status is paid - accept order
                // otherwise just update status in current order
            }
            case StatusEvents.PAID -> {
                log.info("Processing paid status...");
                //
            }
        }
    }
}
