package com.hotel.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;

import com.hotel.notification_service.dto.NotificationMessage;

@Configuration
public class KafkaConsumerConfig {

	@Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationMessage> kafkaListenerContainerFactory(
            ConsumerFactory<String, NotificationMessage> consumerFactory,
            KafkaTemplate<String, NotificationMessage> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, NotificationMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // Retry + recovery configuration
        // Retry: 3 attempts, exponential backoff
        BackOff backOff = new ExponentialBackOff(1000, 2.0); // initial interval 1s, multiplier 2
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
            System.err.println("Message failed after retries: " + record.value());
            // optional: send to dead-letter topic
            kafkaTemplate.send("hotel-notifications-dlt", (NotificationMessage) record.value());
        }, backOff);

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

}

