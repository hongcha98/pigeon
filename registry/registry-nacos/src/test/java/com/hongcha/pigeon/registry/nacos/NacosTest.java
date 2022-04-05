package com.hongcha.pigeon.registry.nacos;

import com.hongcha.pigeon.common.service.Service;
import com.hongcha.pigeon.common.service.ServiceAddress;
import com.hongcha.pigeon.registry.RegistryConfig;
import com.hongcha.pigeon.registry.RegistryMetadata;
import com.hongcha.pigeon.registry.ServiceRegistry;
import com.hongcha.remote.common.spi.SpiLoader;
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
        // 测试是否正常发送心跳
        Thread.sleep(20000);
    }
}
