package io.github.hongcha98.pigeon.registry.nacos;

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
import java.util.Map;
import java.util.Set;

public class NacosTest {
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
            serviceList.add(new Service("com.hongcha.registry.nacos.test." + i, "default", "default"));
        }
        registryConfig = new RegistryConfig("124.222.9.75:8848", "nacos", "nacos");
        Map<String, String> property = registryConfig.getProperty();
        property.put(NacosServiceRegistry.GROUP_NAME, "PIGEON_NACOS_TEST");
        serviceRegistry = SpiLoader.load(ServiceRegistry.class, "nacos", new Class[]{RegistryMetadata.class, RegistryConfig.class}, new Object[]{registryMetadata, registryConfig});
        serviceRegistry.start();
    }

    @Test
    public void foundTest() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            List<ServiceAddress> serviceAddressList = serviceRegistry.foundService(registryMetadata.getApplicationName(), new Service("com.hongcha.registry.nacos.test." + i, "default", "default"));
            for (ServiceAddress serviceAddress : serviceAddressList) {
                Assert.assertEquals(serviceAddress, registryMetadata.getServiceAddress());
            }
        }
    }
}
