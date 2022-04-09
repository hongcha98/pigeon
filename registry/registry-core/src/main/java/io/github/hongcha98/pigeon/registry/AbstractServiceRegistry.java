package io.github.hongcha98.pigeon.registry;

import io.github.hongcha98.pigeon.common.error.PigeonException;

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
            try {
                init();
                doStart();
            } catch (Exception e) {
                throw new PigeonException(e);
            }

        }

    }

    protected abstract void init() throws Exception;

    protected abstract void doStart() throws Exception;

    @Override
    public RegistryMetadata getRegistryMetadata() {
        return registryMetadata;
    }
}
