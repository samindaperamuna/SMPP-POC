package org.fifthgen.smpp.config;

import lombok.Data;

@Data
public abstract class SMPPClientConfig {
    protected String host;
    protected int port;
    protected boolean ssl;
    protected String systemId;
    protected String password;
    protected String systemType;
    protected String charset;
    protected Long bindTimeout;
    protected Integer enquireLinkTimer;
    protected Integer transactionTimer;
}
