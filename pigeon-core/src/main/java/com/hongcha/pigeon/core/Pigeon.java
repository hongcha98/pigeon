package com.hongcha.pigeon.core;

import com.hongcha.pigeon.core.proxy.ServiceProxy;
import com.hongcha.pigeon.core.registry.ServiceRegistry;
import com.hongcha.pigeon.core.registry.impl.ZookeeperServiceRegistry;
import com.hongcha.pigeon.core.remoting.RemotingClient;
import com.hongcha.pigeon.core.remoting.RemotingServer;
import com.hongcha.pigeon.core.service.annotations.PigeonService;
import com.hongcha.pigeon.core.service.handler.ServiceHandler;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.utils.ClassUtil;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Pigeon {
    private PigeonConfig pigeonConfig;

    protected ServiceRegistry serviceRegistry;

    protected RemotingClient remotingClient;

    protected RemotingServer remotingServer;

    public Pigeon(PigeonConfig pigeonConfig) {
        this.pigeonConfig = pigeonConfig;
    }

    public PigeonConfig getPigeonConfig() {
        return pigeonConfig;
    }

    public void start() {
        Map<Service, Object> serviceObjectMap = searchPigeon();
        startRegistry(serviceObjectMap.keySet());
        startRemoting(serviceObjectMap);
    }

    protected void startRemoting(Map<Service, Object> serviceObjectMap) {
        remotingClient = new RemotingClient();
        remotingServer = new RemotingServer(pigeonConfig.getPort(), serviceObjectMap);
        remotingServer.start();
    }

    protected void startRegistry(Set<Service> serviceList) {
        serviceRegistry = new ZookeeperServiceRegistry(pigeonConfig.getPort(), pigeonConfig.getRegistry());
        serviceRegistry.addAllService(serviceList);
        serviceRegistry.start();
    }


    protected Map<Service, Object> searchPigeon() {
        Map<Service, Object> serviceHandlerMap = new HashMap<>();
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
                        e.printStackTrace();
                        return;
                    }
                    if (handler == null) return;
                    Class<?>[] interfaces = cl.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        if (!(anInterface.equals(Object.class) || anInterface.equals(Serializable.class))) {
                            Service service = new Service(anInterface.getName(), group, version);
                            ServiceHandler serviceHandler = new ServiceHandler();
                            serviceHandler.setService(service);
                            serviceHandler.setHandler(handler);
                            serviceHandlerMap.put(service, handler);
                        }
                    }
                });
        return serviceHandlerMap;
    }


    protected Object serviceHandler(Class<?> cl) throws InstantiationException, IllegalAccessException {
        return cl.newInstance();
    }

    public <T> T getProxy(Class<T> clazz) {
        return getProxy(clazz, "default", "default");
    }


    public <T> T getProxy(Class<T> clazz, String group, String version) {
        Service service = new Service(clazz.getName(), group, version);
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new ServiceProxy(service, serviceRegistry, remotingClient));
    }

}
