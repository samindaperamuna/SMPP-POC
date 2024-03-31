package org.fifthgen.smpp.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fifthgen.smpp.config.ConnectionProperties;
import org.fifthgen.smpp.config.SMPPMOClientConfig;
import org.fifthgen.smpp.config.SMPPMTClientConfig;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class SMPPProxyServer {

    private boolean running = true;

    private final SMPPMOClientConfig moConfig;
    private final MessageReceiverListenerMO moReceiverListener;
    private final SessionStateListenerMO moSessionListener;
    private SMPPSessionMO moSession;

    private final SMPPMTClientConfig mtConfig;
    private final MessageReceiverListenerMT mtReceiverListener;
    private final SessionStateListenerMT mtSessionListener;
    private SMPPSessionMT mtSession;

    public void start() {
        startMOSession();
        startMTSession();
    }

    public void stop() {
        try {
            running = false;

            if (this.moSession != null) {
                this.moSession.unbindAndClose();
            }

            if (this.mtSession != null) {
                this.mtSession.unbindAndClose();
            }

            log.info("");
        } catch (Exception e) {
            log.error("Failed to stop server sessions", e);
        }
    }

    private void startMOSession() {
        this.moSession = new SMPPSessionMO(new ConnectionProperties());
        moSession.setMessageReceiverListener(moReceiverListener);
        moSession.addSessionStateListener(moSessionListener);
        moSession.setEnquireLinkTimer(moConfig.getEnquireLinkTimer());

        log.debug("SMPP MO session with id {} started on port {}", moSession.getId(), moConfig.getPort());

        try {
            log.info("Connecting to SMPP MO session with id {} on task {}", moSession.getId(), Thread.currentThread().getName());

            final String systemId = moSession.connectAndBind(
                    moConfig.getHost(),
                    moConfig.getPort(),
                    new BindParameter(BindType.BIND_TRX,
                            moConfig.getSystemId(),
                            moConfig.getPassword(),
                            moConfig.getSystemType(),
                            TypeOfNumber.UNKNOWN,
                            NumberingPlanIndicator.UNKNOWN,
                            null
                    )
            );

            log.info("Connected to SMPP MO session with id {} with SMSC with system id {} on task {}",
                    moSession.getId(),
                    systemId,
                    Thread.currentThread().getName()
            );
        } catch (IOException e) {
            log.error("Failed connect and bind to host {}:{}: {}", moConfig.getHost(), moConfig.getPort(), e.getMessage());
        }
    }

    private void startMTSession() {
        this.mtSession = new SMPPSessionMT(new ConnectionProperties());
        mtSession.setMessageReceiverListener(mtReceiverListener);
        mtSession.addSessionStateListener(mtSessionListener);
        mtSession.setEnquireLinkTimer(mtConfig.getEnquireLinkTimer());

        log.debug("SMPP MT session with id {} started on port {}", mtSession.getId(), mtConfig.getPort());

        try {
            log.info("Connecting to SMPP MT session with id {} on task {}", mtSession.getId(), Thread.currentThread().getName());

            final String systemId = mtSession.connectAndBind(
                    mtConfig.getHost(),
                    mtConfig.getPort(),
                    new BindParameter(BindType.BIND_TRX,
                            mtConfig.getSystemId(),
                            mtConfig.getPassword(),
                            mtConfig.getSystemType(),
                            TypeOfNumber.UNKNOWN,
                            NumberingPlanIndicator.UNKNOWN,
                            null
                    )
            );

            log.info("Connected to SMPP MT session with id {} with SMSC with system id {} on task {}",
                    mtSession.getId(),
                    systemId,
                    Thread.currentThread().getName()
            );

            // Send an initial message to get the connection going
        } catch (IOException e) {
            log.error("Failed connect and bind to host {}:{}: {}", mtConfig.getHost(), mtConfig.getPort(), e.getMessage());
        }
    }

    @RabbitListener(queues = {"${rabbitmq.mo.queue.name}"})
    private void consumeOutboundQueue(DeliverSm message) {
        if (mtSession != null) {
            SubmitSmResp submitSmResp = submitMessage(mtSession, message);
        }
    }

    @RabbitListener(queues = {"${rabbitmq.mt.queue.name}"})
    private void consumeInboundQueue(DeliverSm message) {
        if (moSession != null) {
            SubmitSmResp submitSmResp = submitMessage(moSession, message);
        }
    }

    private SubmitSmResp submitMessage(SMPPSessionImpl session, DeliverSm message) {
        try {
            RegisteredDelivery registeredDelivery = new RegisteredDelivery();
            registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);

            SubmitSmParams submitSmParams = new SubmitSmParams.SubmitSmParamsBuilder()
                    .serviceType(message.getServiceType())
                    .sourceAddrTon(TypeOfNumber.valueOf(message.getSourceAddrTon()))
                    .sourceAddrNpi(NumberingPlanIndicator.valueOf(message.getSourceAddrNpi()))
                    .sourceAddr(message.getSourceAddr())
                    .destAddrTon(TypeOfNumber.valueOf(message.getSourceAddrTon()))
                    .destAddrNpi(NumberingPlanIndicator.valueOf(message.getDestAddrNpi()))
                    .destinationAddr(message.getDestAddress())
                    .esmClass(new ESMClass())
                    .protocolId(message.getProtocolId())
                    .priorityFlag(message.getPriorityFlag())
                    .scheduleDeliveryTime(null)
                    .validityPeriod(null)
                    .registeredDelivery(registeredDelivery)
                    .replaceIfPresentFlag(message.getReplaceIfPresent())
                    .dataCoding(new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false))
                    .smDefaultMsgId(message.getSmDefaultMsgId())
                    .shortMessage(message.getShortMessage())
                    .optionalParameters(message.getOptionalParameters())
                    .build();

            return session.submitShortMessageGetResp(submitSmParams);
        } catch (PDUException e) {
            log.error("Invalid PDU parameter", e);
        } catch (ResponseTimeoutException e) {
            log.error("Response timeout", e);
        } catch (InvalidResponseException e) {
            log.error("Receive invalid response", e);
        } catch (NegativeResponseException e) {
            log.error("Receive negative response", e);
        } catch (IOException e) {
            log.error("I/O error occurred", e);
        }

        return null;
    }
}
