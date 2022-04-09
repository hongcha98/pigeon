package io.github.hongcha98.pigeon.spring.service.impl;


import io.github.hongcha98.pigeon.common.service.annotations.PigeonService;
import io.github.hongcha98.pigeon.spring.service.TestService;

@PigeonService
public class TestServiceImpl implements TestService {

    @Override
    public void echo(String text) {
        System.out.println(text);
    }
}
