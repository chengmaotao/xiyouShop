package com.cms.rabbitmq;

import com.jfinal.kit.PropKit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

public class RabbitMqFactory {

    private static RabbitTemplate rabbitTemplate = null;

    static {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(PropKit.get("rabbitmq.address"));
        connectionFactory.setUsername(PropKit.get("rabbitmq.username"));
        connectionFactory.setPassword(PropKit.get("rabbitmq.password"));
        connectionFactory.setVirtualHost(PropKit.get("rabbitmq.virtualhost"));

        rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    private RabbitMqFactory() {

    }


    public static RabbitTemplate getInstances() {
        return rabbitTemplate;
    }


}
