package com.hongcha.pigeon.example.helloworld.consumer;

import com.hongcha.pigeon.core.Pigeon;
import com.hongcha.pigeon.core.PigeonConfig;
import com.hongcha.pigeon.core.registry.RegistryConfig;
import com.hongcha.pigeon.example.helloworld.service.HelloWorldService;

import java.util.Scanner;

public class ComsumerStart {
    public static void main(String[] args) {
        PigeonConfig pigeonConfig = new PigeonConfig();
        pigeonConfig.setPort(30802);
        pigeonConfig.setPackages(new String[]{});
        pigeonConfig.setRegistry(new RegistryConfig("127.0.0.1:2181", null, null));
        Pigeon pigeon = new Pigeon(pigeonConfig);
        pigeon.start();
        HelloWorldService helloWorldService = pigeon.getProxy(HelloWorldService.class);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String nextLine = scanner.nextLine();
                helloWorldService.print(nextLine);
                System.out.println("helloWorldService.reverse(nextLine) = " + helloWorldService.reverse(nextLine));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
