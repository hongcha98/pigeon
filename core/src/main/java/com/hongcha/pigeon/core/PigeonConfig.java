package com.hongcha.pigeon.core;


import com.hongcha.pigeon.common.service.ServiceAddress;
import com.hongcha.pigeon.registry.RegistryConfig;

public class PigeonConfig {
    private String applicationName;

    private ServiceAddress applicationAddress;

    private String[] packages = new String[]{};

    private RegistryConfig registry;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public ServiceAddress getApplicationAddress() {
        return applicationAddress;
    }

    public void setApplicationAddress(ServiceAddress applicationAddress) {
        this.applicationAddress = applicationAddress;
    }

    public String[] getPackages() {
        return packages;
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
    }

    public RegistryConfig getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registry = registry;
    }
}
