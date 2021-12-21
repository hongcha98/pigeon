package com.hongcha.pigeon.example.helloworld.consumer;

import com.hongcha.pigeon.core.service.annotations.PigeonReference;
import com.hongcha.pigeon.example.helloworld.service.HelloWorldService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class ComsumerSpringStart {
    @PigeonReference
    HelloWorldService helloWorldService;

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ComsumerSpringStart.class, args);
        ComsumerSpringStart comsumerSpringStart = run.getBean(ComsumerSpringStart.class);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(1);
            try {
                String nextLine = scanner.nextLine();
                System.out.println("helloWorldService.valueOf(nextLine) = " + comsumerSpringStart.helloWorldService.valueOf(nextLine));
                System.out.println("comsumerSpringStart.helloWorldService.valueOf(nextLine,null) = " + comsumerSpringStart.helloWorldService.valueOf(nextLine, null));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
