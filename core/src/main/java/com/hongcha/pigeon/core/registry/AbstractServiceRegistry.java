package com.hongcha.pigeon.core.registry;

import com.hongcha.pigeon.core.error.PigeonException;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractServiceRegistry implements ServiceRegistry {

    protected final ServiceAddress localServiceAddress;

    protected final Set<Service> serviceSet;

    protected final RegistryConfig registryConfig;

    protected volatile boolean isStart = false;


    protected AbstractServiceRegistry(int port, RegistryConfig registryConfig) {
        this.localServiceAddress = new ServiceAddress(getIp(), port);
        this.registryConfig = registryConfig;
        this.serviceSet = new HashSet<>();
    }

    protected String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (IOException e) {
            throw new PigeonException("未能正确获取本机地址");
        }
    }


    @Override
    public ServiceAddress getLocalServiceiAddress() {
        return localServiceAddress;
    }


    @Override
    public RegistryConfig getRegistryConfig() {
        return this.registryConfig;
    }

    @Override
    public void addService(Service service) {
        checkStart();
        serviceSet.add(service);
    }

    @Override
    public void addAllService(Collection<Service> serviceCollection) {
        this.serviceSet.addAll(serviceCollection);
    }

    @Override
    public Set<Service> getAllService() {
        return Collections.unmodifiableSet(serviceSet);
    }

    @Override
    public boolean isStart() {
        return isStart;
    }

    @Override
    public synchronized void start() {
        checkStart();
        isStart = true;
        init();
        doStart();
    }

    protected abstract void init();

    protected void doStart() {
        Set<Service> allService = getAllService();
        for (Service service : allService) {
            doRegistry(service);
        }
    }

    protected abstract void doRegistry(Service service);


    protected void checkStart() {
        if (isStart) {
            throw new PigeonException("pigeon has started");
        }
    }

}
