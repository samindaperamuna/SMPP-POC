package org.fifthgen.smpp.proxy;

import lombok.extern.slf4j.Slf4j;
import org.fifthgen.smpp.util.Converters;
import org.jsmpp.bean.*;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;

import static org.jsmpp.SMPPConstant.STAT_ESME_RSYSERR;

@Slf4j
public abstract class MessageReceiverListenerImpl implements MessageReceiverListener {
    @Override
    public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        log.info("deliver_sm seq:{} src:{} {}/{} dst:{} {}/{}",
                deliverSm.getSequenceNumber(),
                deliverSm.getSourceAddr(),
                deliverSm.getSourceAddrTon(),
                deliverSm.getSourceAddrNpi(),
                deliverSm.getDestAddress(),
                deliverSm.getDestAddrTon(),
                deliverSm.getDestAddrNpi()
        );

        log.debug("deliver_sm ESM           {}", Converters.byteToHex(deliverSm.getEsmClass()));
        log.debug("deliver_sm sequence      {}", deliverSm.getSequenceNumber());
        log.debug("deliver_sm service type  {}", deliverSm.getServiceType());
        log.debug("deliver_sm priority flag {}", deliverSm.getPriorityFlag());

        final OptionalParameter[] optionalParameters = deliverSm.getOptionalParameters();
        for (final OptionalParameter parameter : optionalParameters) {
            final byte[] bytes = parameter.serialize();

            log.debug("Optional parameter {}: [{}]", parameter.tag, Converters.encodeHexString(bytes));
        }

        // Handle delivery receipt
        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())
                || MessageType.SME_DEL_ACK.containedIn(deliverSm.getEsmClass())) {

            try {
                log.info("deliver_sm sm : {}", new String(deliverSm.getShortMessage()));

                final OptionalParameter.OctetString jmr = (OptionalParameter.OctetString) deliverSm.getOptionalParameter((short) 8192);
                if (jmr != null) {
                    final String jmrId = jmr.getValueAsString();

                    log.info("deliver_sm jmr: '{}'", jmrId);
                }

                final OptionalParameter.OctetString unknown = (OptionalParameter.OctetString) deliverSm.getOptionalParameter((short) 1542);
                if (unknown != null) {
                    log.info("deliver_sm 1542: [{}]", Converters.encodeHexString(unknown.getValue()));
                }

                final OptionalParameter.OctetString networkMccMnc = (OptionalParameter.OctetString) deliverSm.getOptionalParameter((short) 5472);
                if (networkMccMnc != null) {
                    final String networkMccMncHex = Converters.encodeHexString(networkMccMnc.getValue());

                    log.info("deliver_sm networkMccMnc: '{}'", networkMccMncHex);
                    log.info("deliver_sm networkMccMnc: '{}'", networkMccMnc.getValueAsString());
                }

                final OptionalParameter.Network_error_code networkErrorCode
                        = (OptionalParameter.Network_error_code) deliverSm.getOptionalParameter(
                        OptionalParameter.Tag.NETWORK_ERROR_CODE
                );
                if (networkErrorCode != null) {
                    log.info("deliver_sm network ErrorCode: '{}'", networkErrorCode.getErrorCode());
                    log.info("deliver_sm network type: '{}'", networkErrorCode.getNetworkType().name());
                }
            } catch (RuntimeException e) {
                log.error("Runtime exception", e);

                throw new ProcessRequestException(e.getMessage(), STAT_ESME_RSYSERR);
            }
        } else {
            log.info("Receive message: {}", new String(deliverSm.getShortMessage()));
        }
    }

    @Override
    public void onAcceptAlertNotification(AlertNotification notification) {
        log.info("onAcceptAlertNotification: {} {}", notification.getSourceAddr(), notification.getEsmeAddr());
    }

    @Override
    public DataSmResult onAcceptDataSm(DataSm dataSm, Session session) throws ProcessRequestException {
        log.info("onAcceptDataSm: {} {} {}", session.getSessionId(), dataSm.getSourceAddr(), dataSm.getDestAddress());

        throw new ProcessRequestException("The data_sm is not implemented", STAT_ESME_RSYSERR);
    }
}
