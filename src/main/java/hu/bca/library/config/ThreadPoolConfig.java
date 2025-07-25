package hu.bca.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor poolExecutor = new ThreadPoolTaskExecutor();

        poolExecutor.setThreadNamePrefix("BasicThread-");
        poolExecutor.setCorePoolSize(2);
        poolExecutor.setMaxPoolSize(8);
        poolExecutor.initialize();

        return poolExecutor;
    }
}