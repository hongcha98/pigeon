package com.hongcha.pigeon.registry.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hongcha.pigeon.common.service.Service;
import com.hongcha.pigeon.common.service.ServiceAddress;
import com.hongcha.pigeon.registry.AbstractServiceRegistry;
import com.hongcha.pigeon.registry.RegistryConfig;
import com.hongcha.pigeon.registry.RegistryMetadata;
import com.hongcha.remote.common.spi.SpiDescribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpiDescribe(name = "nacos")
public class NacosServiceRegistry extends AbstractServiceRegistry {
    private static final Logger log = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String METADATA_KEY = "serverList";

    public static final String GROUP_NAME = "groupName";

    private ScheduledExecutorService scheduledExecutorService;

    private NamingService namingService;

    private final String groupName;

    public NacosServiceRegistry(RegistryMetadata registryMetadata, RegistryConfig registryConfig) {
        super(registryMetadata, registryConfig);
        groupName = getRegistryConfig().getProperty().get(GROUP_NAME);
    }

    @Override
    public List<ServiceAddress> foundService(String applicationName, Service service) {
        try {
            List<Instance> instanceList = namingService.getAllInstances(applicationName, groupName);
            return instanceList.stream().filter(instance -> {
                String serverList = instance.getMetadata().get(METADATA_KEY);
                if (serverList == null) return false;
                List<Service> serviceList = JSON.parseArray(serverList, Service.class);
                for (Service s : serviceList) {
                    if (s.equals(service)) {
                        return true;
                    }
                }
                return false;
            }).map(instance -> new ServiceAddress(instance.getIp(), instance.getPort())).collect(Collectors.toList());
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void init() throws Exception {
        RegistryConfig registryConfig = getRegistryConfig();
        Properties properties = new Properties();
        propertiesPut(properties, PropertyKeyConst.NAMESPACE, registryConfig.getProperty().get(PropertyKeyConst.NAMESPACE));
        propertiesPut(properties, PropertyKeyConst.SERVER_ADDR, registryConfig.getAddress());
        propertiesPut(properties, PropertyKeyConst.USERNAME, registryConfig.getUsername());
        propertiesPut(properties, PropertyKeyConst.PASSWORD, registryConfig.getPassword());
        namingService = NacosFactory.createNamingService(properties);
    }

    @Override
    protected void doStart() throws Exception {
        RegistryMetadata registryMetadata = getRegistryMetadata();
        RegistryConfig registryConfig = getRegistryConfig();
        Map<String, String> property = registryConfig.getProperty();
        Instance instance = new Instance();
        instance.setIp(registryMetadata.getServiceAddress().getIp());
        instance.setPort(registryMetadata.getServiceAddress().getPort());
        instance.setClusterName(property.get(PropertyKeyConst.CLUSTER_NAME));
        Map<String, String> metadata = new HashMap<>();
        metadata.put(METADATA_KEY, JSON.toJSONString(registryMetadata.getServiceList()));
        instance.setMetadata(metadata);
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        namingService.registerInstance(registryMetadata.getApplicationName(), groupName, instance);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                namingService.registerInstance(registryMetadata.getApplicationName(), groupName, instance);
            } catch (NacosException e) {
                log.error("nacos registry error", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private static void propertiesPut(Properties properties, Object key, Object value) {
        if (key != null && value != null) {
            properties.put(key, value);
        }
    }

}
