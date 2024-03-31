package org.fifthgen.smpp.proxy;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.extra.ProcessRequestException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiverListenerMO extends MessageReceiverListenerImpl {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.mo.routing.key}")
    private String inboundRoutingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        super.onAcceptDeliverSm(deliverSm);

        // Forward message to RabbitMQ
        rabbitTemplate.convertAndSend(exchange, inboundRoutingKey, deliverSm);
    }
}
