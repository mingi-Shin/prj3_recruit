package kr.co.sist.mail;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

	/**
	 * ThreadPoolTaskExecutor: 백그라운드 작업을 실행할 쓰레드 풀
	 * corePoolSize: 기본 유지 스레드 개수
	 * maxPoolSize: 동시에 처리할 수 있는 최대 스레드 개수
	 * queueCapacity: 대기 가능한 작업 개수
	 */
	@Bean(name = "mailTaskExecutor")
	public Executor mailTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);      // 동시에 최소 2개 실행
    executor.setMaxPoolSize(50);       // 최대 5개 실행
    executor.setQueueCapacity(100);    // 대기열 10개
    executor.setThreadNamePrefix("MailSender-");
    executor.initialize();
    return executor;
	}
}
