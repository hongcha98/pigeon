package com.hongcha.pigeon.service;

public interface HelloWorldService {
    /**
     * 输出入参的字符串
     *
     * @param helloWorld
     */
    void print(String helloWorld);

    /**
     * 反转字符串
     *
     * @param str
     * @return
     */
    String reverse(String str);
}
