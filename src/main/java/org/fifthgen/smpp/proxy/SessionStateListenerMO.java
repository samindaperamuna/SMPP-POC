package org.fifthgen.smpp.proxy;

import lombok.extern.slf4j.Slf4j;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.Session;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SessionStateListenerMO extends SessionStateListenerImpl {

    @Override
    public void onStateChange(SessionState newState, SessionState oldState, Session source) {
        log.info("MO Session {} changed from {} to {}", source.getSessionId(), oldState, newState);

        if (newState == SessionState.CLOSED) {
            // Throw away old session and create new session
        }
    }
}
