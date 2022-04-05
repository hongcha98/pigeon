package com.hongcha.pigeon.core;

import com.hongcha.pigeon.common.error.PigeonException;
import com.hongcha.pigeon.common.service.annotations.PigeonService;
import com.hongcha.pigeon.common.service.handler.ServiceHandlerFactory;
import com.hongcha.pigeon.common.service.metadata.Service;
import com.hongcha.pigeon.core.proxy.ServiceProxy;
import com.hongcha.pigeon.core.utils.ClassUtil;
import com.hongcha.pigeon.registry.RegistryConfig;
import com.hongcha.pigeon.registry.ServiceRegistry;
import com.hongcha.remote.common.spi.SpiLoader;
import com.hongcha.remote.core.RemoteClient;
import com.hongcha.remote.core.RemoteServer;
import com.hongcha.remote.core.config.RemoteConfig;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Set;

@Slf4j
public class Pigeon {
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
        log.info("pigeon start : {}", pigeonConfig);
        try {
            initServiceHandlerFactory();
            startRegistry();
            startRemote();
        } catch (Exception e) {
            log.error("pigeon start error ", e);
            throw new PigeonException(e);
        }
        log.info("pigeon successfully started");
        log.info("pigeon serviceHandlerFactory : {}", serviceHandlerFactory);

    }


    public void close() {
        try {
            remoteClient.close();
        } catch (Exception e) {
            log.error("client close error", e);
        }
        try {
            remoteServer.close();
        } catch (Exception e) {
            log.error("server close error", e);
        }
    }

    protected void startRemote() {
        RemoteConfig RemoteConfig = new RemoteConfig();
        RemoteConfig.setPort(pigeonConfig.getPort());
        remoteServer = new RemoteServer(RemoteConfig);
        remoteClient = new RemoteClient(RemoteConfig);
        try {
            remoteServer.registerProcess(0, new PigeonRequestProcess(serviceHandlerFactory), new NioEventLoopGroup(1));
            remoteServer.start();
            remoteClient.start();
        } catch (Exception e) {
            log.error("start remote error", e);
        }

    }

    protected void startRegistry() {
        serviceRegistry = SpiLoader.load(ServiceRegistry.class, pigeonConfig.getRegistry().getType(), new Class[]{int.class, RegistryConfig.class}, new Object[]{pigeonConfig.getPort(), pigeonConfig.getRegistry()});
        serviceRegistry.addAllService(serviceHandlerFactory.getAll().keySet());
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

    public <T> T getProxy(Class<T> clazz) {
        return getProxy(clazz, "default", "default");
    }


    public <T> T getProxy(Class<T> clazz, String group, String version) {
        Service service = new Service(clazz.getName(), group, version);
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new ServiceProxy(service, serviceRegistry, remoteClient));
    }

}
