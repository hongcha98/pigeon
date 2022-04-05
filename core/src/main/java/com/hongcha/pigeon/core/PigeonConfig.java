package com.hongcha.pigeon.core;


import com.hongcha.pigeon.registry.RegistryConfig;

public class PigeonConfig {
    private int port = 30800;

    private String[] packages = new String[]{};

    private RegistryConfig registry;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
