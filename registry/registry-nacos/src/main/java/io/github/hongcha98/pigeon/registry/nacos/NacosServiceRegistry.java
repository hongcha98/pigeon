package io.github.hongcha98.pigeon.registry.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.github.hongcha98.pigeon.common.error.PigeonException;
import io.github.hongcha98.pigeon.common.service.Service;
import io.github.hongcha98.pigeon.common.service.ServiceAddress;
import io.github.hongcha98.pigeon.registry.AbstractServiceRegistry;
import io.github.hongcha98.pigeon.registry.RegistryConfig;
import io.github.hongcha98.pigeon.registry.RegistryMetadata;
import io.github.hongcha98.remote.common.spi.SpiDescribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@SpiDescribe(name = "nacos")
public class NacosServiceRegistry extends AbstractServiceRegistry {
    public static final String GROUP_NAME = "groupName";
    private static final Logger LOG = LoggerFactory.getLogger(NacosServiceRegistry.class);
    private static final String METADATA_KEY = "serverList";
    private final String groupName;
    private NamingService namingService;

    public NacosServiceRegistry(RegistryMetadata registryMetadata, RegistryConfig registryConfig) {
        super(registryMetadata, registryConfig);
        groupName = getRegistryConfig().getProperty().get(GROUP_NAME);
    }

    private static void propertiesPut(Properties properties, Object key, Object value) {
        if (key != null && value != null) {
            properties.put(key, value);
        }
    }

    @Override
    public List<ServiceAddress> foundService(String applicationName, Service service) {
        Exception exception = null;
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
            exception = e;
        }
        throw new PigeonException(applicationName + " not found " + service.getServiceName() + " provider", exception);
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
        namingService.registerInstance(registryMetadata.getApplicationName(), groupName, instance);
    }

    @Override
    public void close() {
        try {
            namingService.shutDown();
        } catch (NacosException e) {
            LOG.error("namingService close error", e);
        }
    }
}
