package io.github.hongcha98.pigeon.spring;

import io.github.hongcha98.pigeon.core.Pigeon;
import io.github.hongcha98.pigeon.spring.service.TestService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

public class SpringTest {
    ConfigurableApplicationContext provider;
    ConfigurableApplicationContext consumer;

    @Before
    public void init() throws IOException {
        provider = SpringApplication.run(Provider.class);
        consumer = SpringApplication.run(Consumer.class);
    }

    @Test
    public void test() {
        Pigeon providerPigeon = provider.getBean(Pigeon.class);
        Pigeon consumerPigeon = consumer.getBean(Pigeon.class);
        TestService testService = consumerPigeon.getProxy(providerPigeon.getPigeonConfig().getApplicationName(), "random", TestService.class);
        testService.echo("hello world");
    }

    @SpringBootApplication(scanBasePackages = "io.github.hongcha98.pigeon.spring.service")
    @PropertySource(value = "classpath:provider.properties")
    static class Provider {

    }

    @SpringBootApplication(scanBasePackages = "io.github.hongcha98.pigeon.spring.service.consumer")
    @PropertySource(value = "classpath:consumer.properties")
    static class Consumer {

    }

}
