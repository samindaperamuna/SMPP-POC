package org.fifthgen.smpp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class SMPPBeanConfig {

    @Bean
    public TaskExecutor getTaskExecutor() {
        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // Set maximum number of 200 sessions active
        threadPoolTaskExecutor.setCorePoolSize(200);
        threadPoolTaskExecutor.setMaxPoolSize(200);
        threadPoolTaskExecutor.setQueueCapacity(10000);
        threadPoolTaskExecutor.setThreadNamePrefix("task-");

        return threadPoolTaskExecutor;
    }
}
