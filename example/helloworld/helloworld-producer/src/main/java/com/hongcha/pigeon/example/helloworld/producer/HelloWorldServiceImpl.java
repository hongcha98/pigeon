package com.hongcha.pigeon.example.helloworld.producer;

import com.hongcha.pigeon.core.service.annotations.PigeonService;
import com.hongcha.pigeon.example.helloworld.service.HelloWorldService;


@PigeonService
public class HelloWorldServiceImpl implements HelloWorldService {

    @Override
    public void print(String helloWorld) {
        System.out.println(helloWorld);
    }

    @Override
    public String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
