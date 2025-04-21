package com.fitness.ai_service;



import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Bean
    public Queue activityQueue() {
        return new Queue(queueName, true); // durable = true
    }

    @Bean
    public TopicExchange fitnessExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue activityQueue, TopicExchange fitnessExchange) {
        return BindingBuilder
                .bind(activityQueue)
                .to(fitnessExchange)
                .with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

