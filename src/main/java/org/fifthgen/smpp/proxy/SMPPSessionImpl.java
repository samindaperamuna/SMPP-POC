package org.fifthgen.smpp.proxy;

import lombok.Getter;
import org.fifthgen.smpp.config.ConnectionConstants;
import org.fifthgen.smpp.config.ConnectionProperties;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.PDUReader;
import org.jsmpp.PDUSender;
import org.jsmpp.bean.SubmitSmResp;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitSmCommandTask;
import org.jsmpp.session.connection.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SMPPSessionImpl extends SMPPSession {

    private static final AtomicReference<Long> currentTime = new AtomicReference<>(System.currentTimeMillis());

    private final ConnectionProperties connectionProperties;
    private final int timeout;

    @Getter
    private final long id;

    public SMPPSessionImpl(ConnectionProperties connectionProperties) {
        super();

        this.connectionProperties = connectionProperties;
        this.timeout = this.connectionProperties.get(ConnectionConstants.OPERATOR_SUBMIT_SM_TIMEOUT, 35000);
        this.id = currentTime.accumulateAndGet(
                System.currentTimeMillis(), (prev, next) -> next > prev ? next : prev + 1
        );
    }

    public SMPPSessionImpl(PDUSender pduSender,
                           PDUReader pduReader,
                           ConnectionFactory connectionFactory,
                           ConnectionProperties connectionProperties) {

        super(pduSender, pduReader, connectionFactory);

        this.connectionProperties = connectionProperties;
        this.timeout = this.connectionProperties.get(ConnectionConstants.OPERATOR_SUBMIT_SM_TIMEOUT, 35000);
        this.id = currentTime.accumulateAndGet(
                System.currentTimeMillis(), (prev, next) -> next > prev ? next : prev + 1
        );
    }

    public SMPPSessionImpl(String host,
                           int port,
                           BindParameter bindParam,
                           PDUSender pduSender,
                           PDUReader pduReader,
                           ConnectionFactory connectionFactory,
                           ConnectionProperties connectionProperties) throws IOException {

        super(host, port, bindParam, pduSender, pduReader, connectionFactory);

        this.connectionProperties = connectionProperties;
        this.timeout = this.connectionProperties.get(ConnectionConstants.OPERATOR_SUBMIT_SM_TIMEOUT, 35000);
        this.id = currentTime.accumulateAndGet(
                System.currentTimeMillis(), (prev, next) -> next > prev ? next : prev + 1
        );
    }

    public SMPPSessionImpl(String host,
                           int port,
                           BindParameter bindParam,
                           ConnectionProperties connectionProperties) throws IOException {

        super(host, port, bindParam);

        this.connectionProperties = connectionProperties;
        this.timeout = this.connectionProperties.get(ConnectionConstants.OPERATOR_SUBMIT_SM_TIMEOUT, 35000);
        this.id = currentTime.accumulateAndGet(
                System.currentTimeMillis(), (prev, next) -> next > prev ? next : prev + 1
        );
    }

    public SubmitSmResp submitShortMessageGetResp(SubmitSmParams submitSmParams) throws PDUException,
            ResponseTimeoutException, InvalidResponseException, NegativeResponseException, IOException {

        ensureTransmittable("submitShortMessage");

        SubmitSmCommandTask submitSmTask = new SubmitSmCommandTask(pduSender(),
                submitSmParams.serviceType(),
                submitSmParams.sourceAddrTon(),
                submitSmParams.sourceAddrNpi(),
                submitSmParams.sourceAddr(),
                submitSmParams.destAddrTon(),
                submitSmParams.destAddrNpi(),
                submitSmParams.destinationAddr(),
                submitSmParams.esmClass(),
                submitSmParams.protocolId(),
                submitSmParams.priorityFlag(),
                submitSmParams.scheduleDeliveryTime(),
                submitSmParams.validityPeriod(),
                submitSmParams.registeredDelivery(),
                submitSmParams.replaceIfPresentFlag(),
                submitSmParams.dataCoding(),
                submitSmParams.smDefaultMsgId(),
                submitSmParams.shortMessage(),
                submitSmParams.optionalParameters()
        );

        return (SubmitSmResp) executeSendCommand(submitSmTask, timeout);
    }
}
