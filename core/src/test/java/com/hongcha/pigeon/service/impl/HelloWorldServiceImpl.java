package com.hongcha.pigeon.service.impl;

import com.hongcha.pigeon.common.service.annotations.PigeonService;
import com.hongcha.pigeon.service.HelloWorldService;

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
