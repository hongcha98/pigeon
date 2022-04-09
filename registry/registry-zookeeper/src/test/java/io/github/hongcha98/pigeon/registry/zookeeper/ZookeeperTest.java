package io.github.hongcha98.pigeon.registry.zookeeper;


import io.github.hongcha98.pigeon.common.service.Service;
import io.github.hongcha98.pigeon.common.service.ServiceAddress;
import io.github.hongcha98.pigeon.registry.RegistryConfig;
import io.github.hongcha98.pigeon.registry.RegistryMetadata;
import io.github.hongcha98.pigeon.registry.ServiceRegistry;
import io.github.hongcha98.remote.common.spi.SpiLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class ZookeeperTest {

    private ServiceRegistry serviceRegistry;

    private RegistryMetadata registryMetadata;

    private RegistryConfig registryConfig;

    @Before
    public void before() {
        registryMetadata = new RegistryMetadata();
        registryMetadata.setServiceAddress(new ServiceAddress("127.0.0.1", 9876));
        registryMetadata.setApplicationName("serviceNodeA");
        Set<Service> serviceList = registryMetadata.getServiceList();
        for (int i = 0; i < 10; i++) {
            serviceList.add(new Service("com.hongcha.registry.zk.test." + i, "default", "default"));
        }
        registryConfig = new RegistryConfig("127.0.0.1:2181", null, null);
        serviceRegistry = SpiLoader.load(ServiceRegistry.class, "zookeeper", new Class[]{RegistryMetadata.class, RegistryConfig.class}, new Object[]{registryMetadata, registryConfig});
        serviceRegistry.start();
    }

    @Test
    public void foundTest() {
        for (int i = 0; i < 10; i++) {
            List<ServiceAddress> serviceAddressList = serviceRegistry.foundService(registryMetadata.getApplicationName(), new Service("com.hongcha.registry.zk.test." + i, "default", "default"));
            for (ServiceAddress serviceAddress : serviceAddressList) {
                Assert.assertEquals(serviceAddress, registryMetadata.getServiceAddress());
            }
        }

    }


}
