package org.fifthgen.smpp.proxy;

import lombok.Builder;
import org.jsmpp.bean.*;

@Builder
public record SubmitSmParams(String serviceType,
                             TypeOfNumber sourceAddrTon,
                             NumberingPlanIndicator sourceAddrNpi,
                             String sourceAddr,
                             TypeOfNumber destAddrTon,
                             NumberingPlanIndicator destAddrNpi,
                             String destinationAddr,
                             ESMClass esmClass,
                             byte protocolId,
                             byte priorityFlag,
                             String scheduleDeliveryTime,
                             String validityPeriod,
                             RegisteredDelivery registeredDelivery,
                             byte replaceIfPresentFlag,
                             DataCoding dataCoding,
                             byte smDefaultMsgId,
                             byte[] shortMessage,
                             OptionalParameter... optionalParameters) {
}
