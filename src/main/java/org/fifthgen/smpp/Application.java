package org.fifthgen.smpp;

import lombok.extern.slf4j.Slf4j;
import org.fifthgen.smpp.proxy.SMPPProxyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Slf4j
@EnableAsync
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private SMPPProxyServer server;

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplicationBuilder(Application.class)
                .headless(true)
                .web(WebApplicationType.NONE)
                .build();

        application.run(args);
    }

    public void run(String[] args) throws Exception {
        log.info("**********************************************");
        log.info("Starting the commandline runner main thread");
        log.info("**********************************************");

        log.info("java.version       : {}", System.getProperty("java.version"));
        log.info("java.vendor        : {}", System.getProperty("java.vendor"));
        log.info("java.home          : {}", System.getProperty("java.home"));
        log.info("java.class.path    : {}", System.getProperty("java.class.path"));
        log.info("user.dir           : {}", System.getProperty("user.dir"));
        log.info("user.name          : {}", System.getProperty("user.name"));
        log.info("default timezone   : {}", TimeZone.getDefault().getDisplayName());
        log.info("default locale     : {}", Locale.getDefault().getDisplayName());

        for (String arg : args) {
            log.info("cmdline argument   : " + arg);
        }

        log.info("context environment: {}", ctx.getEnvironment());
        log.info("context start      : {}", new Date(ctx.getStartupDate()));

        // Start the server
        server.start();
    }
}
