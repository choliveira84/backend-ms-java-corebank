package com.corebank.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "corebank.events.exchange";
    public static final String QUEUE_NAME = "transaction.authorized.queue";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
    
    @Bean
    public Queue authorizedQueue() {
        return new Queue(QUEUE_NAME, true);
    }
    
    @Bean
    public Binding binding(Queue authorizedQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(authorizedQueue).to(eventsExchange).with("transaction.authorized");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
