package org.fifthgen.smpp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "smpp.mt")
public class SMPPMTClientConfig extends SMPPClientConfig {
}
