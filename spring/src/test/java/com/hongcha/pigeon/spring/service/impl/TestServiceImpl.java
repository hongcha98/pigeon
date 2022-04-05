package com.hongcha.pigeon.spring.service.impl;


import com.hongcha.pigeon.common.service.annotations.PigeonService;
import com.hongcha.pigeon.spring.service.TestService;

@PigeonService
public class TestServiceImpl implements TestService {

    @Override
    public void echo(String text) {
        System.out.println(text);
    }
}
