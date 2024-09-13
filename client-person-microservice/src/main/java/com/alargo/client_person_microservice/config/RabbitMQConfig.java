package com.alargo.client_person_microservice.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    public static final String QUEUE_NAME_CUSTOMER = "customer.queue";
    public static final String QUEUE_NAME_REPORT = "report.queue";
    public static final String EXCHANGE_NAME = "exchange_name";
    public static final String ROUTING_KEY_CUSTOMER = "routing_key_customer";
    public static final String ROUTING_KEY_REPORT = "routing_key_report";

    @Bean
    public Queue customerQueue() {
        return new Queue(QUEUE_NAME_CUSTOMER, true);
    }

    @Bean
    public Queue reportQueue() {
        return new Queue(QUEUE_NAME_REPORT, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding customerBinding(Queue customerQueue, TopicExchange exchange) {
        return BindingBuilder.bind(customerQueue).to(exchange).with(ROUTING_KEY_CUSTOMER);
    }

    @Bean
    public Binding reportBinding(Queue reportQueue, TopicExchange exchange) {
        return BindingBuilder.bind(reportQueue).to(exchange).with(ROUTING_KEY_REPORT);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}