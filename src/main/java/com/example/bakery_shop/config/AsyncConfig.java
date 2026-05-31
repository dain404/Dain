package com.example.bakery_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Cấu hình xử lý bất đồng bộ @Async.
 * Dùng cho: gửi email xác nhận đơn hàng, thông báo qua WebSocket.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Bean executor cho các tác vụ @Async.
     * - corePoolSize: số thread luôn sẵn sàng
     * - maxPoolSize: số thread tối đa
     * - queueCapacity: hàng đợi khi thread đầy
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);          // 5 thread luôn sẵn sàng
        executor.setMaxPoolSize(20);          // Tối đa 20 thread
        executor.setQueueCapacity(100);       // Hàng đợi 100 task
        executor.setThreadNamePrefix("BakeryAsync-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
