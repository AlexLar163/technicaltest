package com.alargo.account_movements_microservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    public static final String QUEUE_NAME_REPORT = "queue_name_report";

    @Value(QUEUE_NAME_REPORT)
    private String queueNameReport;


    @Bean
    public Queue queue() {
        return new Queue(queueNameReport, true);
    }
}