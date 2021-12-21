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
    public void print(Object obj) {
        System.out.println(obj.toString());
    }

    @Override
    public Long valueOf(String str) {
        return Long.valueOf(str);
    }

    @Override
    public Long valueOf(String str, Object o) {
        return Long.valueOf(str);
    }

    @Override
    public String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
