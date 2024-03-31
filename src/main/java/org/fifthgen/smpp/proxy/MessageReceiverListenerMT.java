package org.fifthgen.smpp.proxy;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.extra.ProcessRequestException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiverListenerMT extends MessageReceiverListenerImpl {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.mt.routing.key}")
    private String outboundRoutingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        super.onAcceptDeliverSm(deliverSm);

        // Forward message to RabbitMQ
        rabbitTemplate.convertAndSend(exchange, outboundRoutingKey, deliverSm);
    }
}
