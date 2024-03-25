package org.fifthgen.smpp.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fifthgen.smpp.config.ConnectionProperties;
import org.fifthgen.smpp.config.SMPPMOClientConfig;
import org.fifthgen.smpp.config.SMPPMTClientConfig;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.BindParameter;
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

    @Async
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

            // Send an initial message to get the connection going
//            try {
//                RegisteredDelivery registeredDelivery = new RegisteredDelivery();
//                registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);
//
//                SubmitSmParams submitSmParams = new SubmitSmParams.SubmitSmParamsBuilder()
//                        .serviceType("CMT")
//                        .sourceAddrTon(TypeOfNumber.UNKNOWN)
//                        .sourceAddrNpi(NumberingPlanIndicator.UNKNOWN)
//                        .sourceAddr("1000")
//                        .destAddrTon(TypeOfNumber.UNKNOWN)
//                        .destAddrNpi(NumberingPlanIndicator.UNKNOWN)
//                        .destinationAddr("2000")
//                        .esmClass(new ESMClass())
//                        .protocolId((byte) 0)
//                        .priorityFlag((byte) 1)
//                        .scheduleDeliveryTime(null)
//                        .validityPeriod(null)
//                        .registeredDelivery(registeredDelivery)
//                        .replaceIfPresentFlag((byte) 0)
//                        .dataCoding(new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false))
//                        .smDefaultMsgId((byte) 0)
//                        .shortMessage("Initialize MO connection".getBytes(moConfig.getCharset()))
//                        .build();
//
//                TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();
//                // moSession.submitShortMessageGetResp(submitSmParams);
//                moSession.submitShortMessage("CMT", TypeOfNumber.INTERNATIONAL,
//                        NumberingPlanIndicator.UNKNOWN, "1616", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN,
//                        "628176504657", new ESMClass(), (byte) 0, (byte) 1, TIME_FORMATTER.format(new Date()), null,
//                        registeredDelivery, (byte) 0, new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1,
//                                false), (byte) 0, "Initialize MO connection".getBytes());
//            } catch (PDUException e) {
//                // Invalid PDU parameter
//                log.error("Invalid PDU parameter", e);
//            } catch (ResponseTimeoutException e) {
//                // Response timeout
//                log.error("Response timeout", e);
//            } catch (InvalidResponseException e) {
//                // Invalid response
//                log.error("Receive invalid response", e);
//            } catch (NegativeResponseException e) {
//                // Receiving negative response (non-zero command_status)
//                log.error("Receive negative response", e);
//            } catch (IOException e) {
//                log.error("I/O error occurred", e);
//            }
        } catch (IOException e) {
            log.error("Failed connect and bind to host {}:{}: {}", moConfig.getHost(), moConfig.getPort(), e.getMessage());
        }
    }

    private void startMTSession() {
        this.mtSession = new SMPPSessionMT(new ConnectionProperties());
    }
}
