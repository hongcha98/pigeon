package com.hongcha.pigeon.registry;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractServiceRegistry implements ServiceRegistry {

    protected final RegistryMetadata registryMetadata;

    protected final RegistryConfig registryConfig;

    protected AtomicBoolean isStart = new AtomicBoolean(false);


    protected AbstractServiceRegistry(RegistryMetadata registryMetadata, RegistryConfig registryConfig) {
        this.registryMetadata = registryMetadata;
        this.registryConfig = registryConfig;

    }


    @Override
    public RegistryConfig getRegistryConfig() {
        return this.registryConfig;
    }


    @Override
    public synchronized void start() {
        if (isStart.compareAndSet(false, true)) {
            init();
            doStart();
        }

    }

    protected abstract void init();

    protected abstract void doStart();

    @Override
    public RegistryMetadata getRegistryMetadata() {
        return registryMetadata;
    }
}
