package com.hongcha.pigeon.example.helloworld.producer;

import com.hongcha.pigeon.core.PigeonConfig;
import com.hongcha.pigeon.core.service.annotations.PigeonReference;
import com.hongcha.pigeon.example.helloworld.service.HelloWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ProducerSpringStart {
    @PigeonReference
    HelloWorldService helloWorldService;

    @Autowired
    PigeonConfig pigeonConfigAdapter;

    @PostConstruct
    public void init(){
        System.out.println("helloWorldService.reverse(\"哈哈哈\") = " + helloWorldService.reverse("哈哈哈"));
        System.out.println(pigeonConfigAdapter);
    }

    public static void main(String[] args) {
         SpringApplication.run(ProducerSpringStart.class, args);
    }
}
