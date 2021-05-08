package com.hongcha.pigeon.example.helloworld.producer;

import com.hongcha.pigeon.core.Pigeon;
import com.hongcha.pigeon.core.PigeonConfig;
import com.hongcha.pigeon.core.registry.RegistryConfig;

public class ProducerStart {
    public static void main(String[] args) {
        PigeonConfig pigeonConfig = new PigeonConfig();
        pigeonConfig.setPort(30801);
        pigeonConfig.setPackages(new String[]{"com.hongcha.pigeon.example.helloworld.producer"});
        pigeonConfig.setRegistry(new RegistryConfig("127.0.0.1:2181", null, null));
        Pigeon pigeon = new Pigeon(pigeonConfig);
        pigeon.start();
    }
}
