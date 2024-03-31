package org.fifthgen.smpp.proxy;

import lombok.extern.slf4j.Slf4j;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.Session;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
public class SessionStateListenerMT extends SessionStateListenerImpl {

    public SessionStateListenerMT(SMPPProxyServer server, long reconnectTimeout) {
        this.server = server;
        this.reconnectTimeout = reconnectTimeout;
    }

    public void onStateChange(SessionState newState, SessionState oldState, final Session session) {
        log.info("MT Session {} changed from {} to {}", session.getSessionId(), oldState, newState);

        if (newState == SessionState.CLOSED) {
            // Throw away old session and create new session
            new Thread(() -> {
                server.startMOSession();

                log.info("Schedule reconnect MT session after {} millis", reconnectTimeout);

                try {
                    Thread.sleep(this.reconnectTimeout);
                } catch (InterruptedException e) {
                    log.error("MT session reconnect timeout interrupted: ", e);
                }

                int attempt = 0;
                while (session.getSessionState().equals(SessionState.CLOSED)) {
                    log.info("Reconnecting MT session attempt #{} ...", ++attempt);

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
