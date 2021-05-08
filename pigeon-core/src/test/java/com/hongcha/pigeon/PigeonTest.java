package com.hongcha.pigeon;

import com.hongcha.pigeon.core.Pigeon;
import com.hongcha.pigeon.core.PigeonConfig;
import com.hongcha.pigeon.core.registry.RegistryConfig;
import com.hongcha.pigeon.service.HelloWorldService;
import org.junit.Test;

public class PigeonTest {
    @Test
    public void pigeonTest() {
        PigeonConfig pigeonConfig = new PigeonConfig();
        pigeonConfig.setPackages(new String[]{"com.hongcha.pigeon.service.impl"});
        pigeonConfig.setRegistry(new RegistryConfig("127.0.0.1:2181", null, null));
        Pigeon pigeon = new Pigeon(pigeonConfig);
        pigeon.start();
        HelloWorldService object = pigeon.getProxy(HelloWorldService.class);
        object.print("Hello World");
        String reverse = object.reverse("pigeon - rpc");
        System.out.println(reverse);
    }
}
