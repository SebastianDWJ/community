package com.mybbs.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Async
public class ThreadPoolConfig {
}
