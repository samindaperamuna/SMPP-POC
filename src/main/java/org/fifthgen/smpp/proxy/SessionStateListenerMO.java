package org.fifthgen.smpp.proxy;

import lombok.extern.slf4j.Slf4j;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.Session;
import org.springframework.stereotype.Component;

@Slf4j
public class SessionStateListenerMO extends SessionStateListenerImpl {

    public SessionStateListenerMO(SMPPProxyServer server, long reconnectTimeout) {
        this.server = server;
        this.reconnectTimeout = reconnectTimeout;
    }

    @Override
    public void onStateChange(SessionState newState, SessionState oldState, Session session) {
        log.info("MO Session {} changed from {} to {}", session.getSessionId(), oldState, newState);

        if (newState == SessionState.CLOSED) {
            // Throw away old session and create new session
            new Thread(() -> {
                server.startMOSession();

                log.info("Schedule reconnect MO session after {} millis", this.reconnectTimeout);

                try {
                    Thread.sleep(this.reconnectTimeout);
                } catch (InterruptedException e) {
                    log.error("MO session reconnect timeout interrupted: ", e);
                }

                int attempt = 0;
                while (session.getSessionState().equals(SessionState.CLOSED)) {
                    log.info("Reconnecting MO session attempt #{} ...", ++attempt);

                    server.startMTSession();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ee) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }
}
