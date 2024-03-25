package org.fifthgen.smpp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "demo")
public class ConfigProperties {

    private int numberOfClientSessions = 5;
    private int minMessagesPerSession = 1;
    private int maxMessagesPerSession = 10;
}
