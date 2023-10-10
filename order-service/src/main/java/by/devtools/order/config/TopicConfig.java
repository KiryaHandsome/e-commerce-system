package by.devtools.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name("order-topic")
                .partitions(1)
                .build();
    }

    @Bean
    public NewTopic orderStatus() {
        return TopicBuilder.name("order-status-topic")
                .partitions(1)
                .build();
    }

}
