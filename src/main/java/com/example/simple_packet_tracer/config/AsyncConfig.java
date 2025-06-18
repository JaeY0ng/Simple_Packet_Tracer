package com.example.simple_packet_tracer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // 스레드 풀 생성
        executor.setCorePoolSize(20); // 기본 스레드 개수 (20개 까지 동시 실행 )
        executor.setMaxPoolSize(50); // 최대 스레드 개수 (최대로 50개 까지 늘어남)
        executor.setQueueCapacity(1000); // 대기 큐 용량 (스레드가 부족하면 대기열에 1000개 까지 저장 가능 )
        executor.setThreadNamePrefix("Async-"); // 스레드 이름 앞에 붙는 접두사 ( 디버깅용 )
        executor.initialize(); // 스레드 풀 초기화
        return executor;
    }
}
