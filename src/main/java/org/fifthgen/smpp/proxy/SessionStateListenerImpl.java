package org.fifthgen.smpp.proxy;

import lombok.extern.slf4j.Slf4j;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.Session;
import org.jsmpp.session.SessionStateListener;

@Slf4j
public abstract class SessionStateListenerImpl implements SessionStateListener {

    @Override
    public void onStateChange(SessionState newState, SessionState oldState, Session source) {
        log.info("MO session {} changed from {} to {}", source.getSessionId(), oldState, newState);

        if (newState == SessionState.CLOSED) {
            // Throw away old session and create new session
        }
    }
}
