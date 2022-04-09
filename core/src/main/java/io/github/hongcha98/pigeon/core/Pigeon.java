package io.github.hongcha98.pigeon.core;

import io.github.hongcha98.pigeon.common.error.PigeonException;
import io.github.hongcha98.pigeon.common.service.Service;
import io.github.hongcha98.pigeon.common.service.annotations.PigeonService;
import io.github.hongcha98.pigeon.common.service.handler.ServiceHandlerFactory;
import io.github.hongcha98.pigeon.core.proxy.ServiceProxy;
import io.github.hongcha98.pigeon.core.utils.ClassUtil;
import io.github.hongcha98.pigeon.registry.RegistryConfig;
import io.github.hongcha98.pigeon.registry.RegistryMetadata;
import io.github.hongcha98.pigeon.registry.ServiceRegistry;
import io.github.hongcha98.remote.common.spi.SpiLoader;
import io.github.hongcha98.remote.core.RemoteClient;
import io.github.hongcha98.remote.core.RemoteServer;
import io.github.hongcha98.remote.core.config.RemoteConfig;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Set;

public class Pigeon {
    private static final Logger LOG = LoggerFactory.getLogger(Pigeon.class);
    protected ServiceRegistry serviceRegistry;
    protected RemoteClient remoteClient;
    protected RemoteServer remoteServer;
    ServiceHandlerFactory serviceHandlerFactory = new ServiceHandlerFactory();
    private PigeonConfig pigeonConfig;

    public Pigeon(PigeonConfig pigeonConfig) {
        this.pigeonConfig = pigeonConfig;
    }

    public PigeonConfig getPigeonConfig() {
        return pigeonConfig;
    }

    public void start() {
        LOG.info("pigeon start : {}", pigeonConfig);
        try {
            initServiceHandlerFactory();
            startRegistry();
            startRemote();
        } catch (Exception e) {
            LOG.error("pigeon start error ", e);
            throw new PigeonException(e);
        }
        LOG.info("pigeon successfully started");
        LOG.info("pigeon serviceHandlerFactory : {}", serviceHandlerFactory);

    }


    public void close() {
        try {
            serviceRegistry.close();
            remoteServer.close();
            remoteClient.close();
        } catch (Exception e) {
            LOG.error("pigeon close error", e);
        }
    }

    protected void startRemote() {
        RemoteConfig RemoteConfig = new RemoteConfig();
        RemoteConfig.setPort(pigeonConfig.getApplicationAddress().getPort());
        remoteServer = new RemoteServer(RemoteConfig);
        remoteClient = new RemoteClient(RemoteConfig);
        remoteServer.registerProcess(0, new PigeonRequestProcess(serviceHandlerFactory), new NioEventLoopGroup(1));
        remoteServer.start();
        remoteClient.start();
    }

    protected void startRegistry() {
        RegistryMetadata registryMetadata = new RegistryMetadata();
        registryMetadata.setApplicationName(pigeonConfig.getApplicationName());
        registryMetadata.setServiceAddress(pigeonConfig.getApplicationAddress());
        registryMetadata.setServiceList(serviceHandlerFactory.getAll().keySet());
        serviceRegistry = SpiLoader.load(ServiceRegistry.class, pigeonConfig.getRegistry().getType(), new Class[]{RegistryMetadata.class, RegistryConfig.class}, new Object[]{registryMetadata, pigeonConfig.getRegistry()});
        serviceRegistry.start();
    }


    protected void initServiceHandlerFactory() {
        String[] scanPackages = pigeonConfig.getPackages();
        Set<Class<?>> classes = ClassUtil.getClasses(scanPackages);
        classes
                .stream()
                .filter(cl -> !cl.isInterface() && cl.isAnnotationPresent(PigeonService.class))
                .forEach(cl -> {
                    PigeonService annotation = cl.getAnnotation(PigeonService.class);
                    String group = annotation.group();
                    String version = annotation.version();
                    Object handler;
                    try {
                        handler = serviceHandler(cl);
                    } catch (Exception e) {
                        return;
                    }
                    if (handler == null) return;
                    Class<?>[] interfaces = cl.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        if (!(anInterface.equals(Object.class) || anInterface.equals(Serializable.class))) {
                            Service service = new Service(anInterface.getName(), group, version);
                            serviceHandlerFactory.register(service, handler);
                        }
                    }
                });

    }


    protected Object serviceHandler(Class<?> cl) throws InstantiationException, IllegalAccessException {
        return cl.newInstance();
    }

    public <T> T getProxy(String applicationName, String loadBalance, Class<T> clazz) {
        return getProxy(clazz, applicationName, "default", "default", loadBalance);
    }


    public <T> T getProxy(Class<T> clazz, String applicationName, String group, String version, String loadBalance) {
        Service service = new Service(clazz.getName(), group, version);
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new ServiceProxy(applicationName, service, loadBalance, serviceRegistry, remoteClient));
    }

}
