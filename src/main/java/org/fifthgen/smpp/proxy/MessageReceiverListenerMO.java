package org.fifthgen.smpp.proxy;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.extra.ProcessRequestException;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiverListenerMO extends MessageReceiverListenerImpl {

    @Override
    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        super.onAcceptDeliverSm(deliverSm);


    }
}
