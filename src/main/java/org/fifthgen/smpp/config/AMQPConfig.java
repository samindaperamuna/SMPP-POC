package org.fifthgen.smpp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AMQPConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.mo.queue.name}")
    private String moQueue;

    @Value("${rabbitmq.mo.routing.key}")
    private String inboundRoutingKey;

    @Value("${rabbitmq.mt.queue.name}")
    private String mtQueue;

    @Value("${rabbitmq.mt.routing.key}")
    private String outboundRoutingKey;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    @Qualifier("moQueue")
    public Queue moQueue() {
        return new Queue(moQueue);
    }

    @Bean
    @Qualifier("mtQueue")
    public Queue mtQueue() {
        return new Queue(mtQueue);
    }

    @Bean
    @Qualifier("moBinding")
    public Binding moBinding() {
        return BindingBuilder.bind(moQueue())
                .to(exchange())
                .with(inboundRoutingKey);
    }

    @Bean
    @Qualifier("mtBinding")
    public Binding mtBinding() {
        return BindingBuilder.bind(mtQueue())
                .to(exchange())
                .with(outboundRoutingKey);
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("org.jsmpp.*"));

        return converter;
    }

//    @Bean
//    public MessageConverter jsonConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jsonConverter());
//
//        return rabbitTemplate;
//    }
}
