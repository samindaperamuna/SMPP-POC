package org.fifthgen.smpp.proxy;

import org.jsmpp.session.SessionStateListener;

public abstract class SessionStateListenerImpl implements SessionStateListener {

    protected long reconnectTimeout;
    protected SMPPProxyServer server;
}
