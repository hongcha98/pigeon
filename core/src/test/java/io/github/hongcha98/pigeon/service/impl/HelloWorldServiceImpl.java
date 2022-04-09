package io.github.hongcha98.pigeon.service.impl;

import io.github.hongcha98.pigeon.common.service.annotations.PigeonService;
import io.github.hongcha98.pigeon.service.HelloWorldService;

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
