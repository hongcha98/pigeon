package io.github.hongcha98.pigeon;

import io.github.hongcha98.pigeon.common.service.ServiceAddress;
import io.github.hongcha98.pigeon.core.Pigeon;
import io.github.hongcha98.pigeon.core.PigeonConfig;
import io.github.hongcha98.pigeon.registry.RegistryConfig;
import io.github.hongcha98.pigeon.service.HelloWorldService;
import org.junit.Test;

public class PigeonTest {
    @Test
    public void pigeonTest() {
        PigeonConfig pigeonConfig = new PigeonConfig();
        pigeonConfig.setApplicationName("pigeon-test");
        pigeonConfig.setApplicationAddress(new ServiceAddress("127.0.0.1", 9999));
        pigeonConfig.setPackages(new String[]{"io.github.hongcha98.pigeon.service.impl"});
        pigeonConfig.setRegistry(new RegistryConfig("127.0.0.1:2181", null, null));
        Pigeon pigeon = new Pigeon(pigeonConfig);
        pigeon.start();
        HelloWorldService object = pigeon.getProxy(pigeonConfig.getApplicationName(), "random", HelloWorldService.class);
        object.print("Hello World");
        String reverse = object.reverse("pigeon - rpc");
        System.out.println(reverse);
    }
}
