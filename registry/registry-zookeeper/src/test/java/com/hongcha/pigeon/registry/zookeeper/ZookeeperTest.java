package com.hongcha.pigeon.registry.zookeeper;


import com.hongcha.pigeon.common.service.metadata.Service;
import com.hongcha.pigeon.common.service.metadata.ServiceAddress;
import com.hongcha.pigeon.registry.RegistryConfig;
import com.hongcha.pigeon.registry.ServiceRegistry;
import com.hongcha.remote.common.spi.SpiLoader;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperTest {
    private ServiceRegistry serviceRegistry;

    @Before
    public void before() {
        serviceRegistry = SpiLoader.load(ServiceRegistry.class, "zookeeper", new Class[]{int.class, RegistryConfig.class}, new Object[]{8081,new RegistryConfig("127.0.0.1:", null, null)});
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
