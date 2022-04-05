package com.hongcha.pigeon.registry;

import com.hongcha.pigeon.core.registry.RegistryConfig;
import com.hongcha.pigeon.core.registry.ServiceRegistry;
import com.hongcha.pigeon.core.registry.impl.ZookeeperServiceRegistry;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;
import org.junit.Before;
import org.junit.Test;

public class ZKTest {
    private ServiceRegistry serviceRegistry;

    @Before
    public void before() {
        serviceRegistry = new ZookeeperServiceRegistry(8081, new RegistryConfig("127.0.0.1:", null, null));
        for (int i = 0; i < 10; i++) {
            serviceRegistry.addService(new Service("com.hongcha.registry.zk.test." + i, "default", "default"));
        }
        serviceRegistry.start();
    }

    @Test
    public void foundTest() {
        ServiceAddress serviceAddress = serviceRegistry.foundService(new Service("com.hongcha.registry.zk.test.3", "default", "default"));
        System.out.println(serviceAddress);
    }


}
