package com.hongcha.pigeon.core.registry.impl;

import com.hongcha.pigeon.core.error.PigeonException;
import com.hongcha.pigeon.core.registry.AbstractServiceRegistry;
import com.hongcha.pigeon.core.registry.RegistryConfig;
import com.hongcha.pigeon.core.service.metadata.Service;
import com.hongcha.pigeon.core.service.metadata.ServiceAddress;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class ZookeeperServiceRegistry extends AbstractServiceRegistry implements Watcher {
    private ZooKeeper zooKeeper;

    private Helper helper = new Helper();

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final static String PIGEON_PATH = "/pigeon";

    public ZookeeperServiceRegistry(int port, RegistryConfig registryConfig) {
        super(port, registryConfig);
    }

    @Override
    protected void init() {
        try {
            zooKeeper = new ZooKeeper(getRegistryConfig().getAddress(), 5000, this);
            countDownLatch.await();
            helper.create(PIGEON_PATH, "pigeon-rpc", CreateMode.PERSISTENT);
        } catch (Exception e) {
            throw new PigeonException("zk error", e);
        }
    }

    @Override
    protected void doRegistry(Service service) {
        String servicePath = registryService(service.getServiceName());
        registryRelease(servicePath, service.getGroup(), service.getVersion());
    }


    protected String registryService(String serviceName) {
        return helper.create(getServicePath(serviceName), "", CreateMode.PERSISTENT);
    }

    private String getServicePath(String serviceName) {
        return PIGEON_PATH + "/" + serviceName;
    }

    protected void registryRelease(String servicePath, String group, String version) {
        helper.create(servicePath + "/" + groupVersionPathName(group, version), getLocalServiceiAddress().toString(), CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    private String groupVersionPathName(String group, String version) {
        return group + "-" + version + "-";
    }


    @Override
    public ServiceAddress foundService(Service service) {
        String servicePath = getServicePath(service.getServiceName());
        if (helper.exists(servicePath)) {
            try {
                List<String> children = zooKeeper.getChildren(servicePath, true);
                List<String> serviceList = children
                        .stream()
                        .filter(path -> path.startsWith(groupVersionPathName(service.getGroup(), service.getVersion())))
                        .collect(Collectors.toList());
                if (!serviceList.isEmpty()) {
                    String targetSericePath = serviceList.get(new Random().nextInt(serviceList.size()));
                    String data = helper.getData(servicePath + "/" + targetSericePath);
                    String[] split = data.split(":");
                    return new ServiceAddress(split[0], Integer.parseInt(split[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new PigeonException("未找到" + service.getServiceName() + "实现");
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        countDownLatch.countDown();
    }


    class Helper {
        boolean exists(String path) {
            return getStat(path) != null;
        }

        Stat getStat(String path) {
            try {
                return zooKeeper.exists(path, true);
            } catch (Exception e) {
                throw new PigeonException("zk error", e);
            }
        }


        String create(String path, String data, CreateMode createMode) {
            try {
                if (!exists(path)) {
                    return zooKeeper.create(path, toByte(data), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                }
                return path;
            } catch (Exception e) {
                throw new PigeonException("zk error", e);
            }
        }


        String getData(String path) {
            Stat stat = getStat(path);
            try {
                return stat == null ? null : toStr(zooKeeper.getData(path, true, stat));
            } catch (Exception e) {
                throw new PigeonException("zk error", e);
            }
        }


        byte[] toByte(String str) {
            return str.getBytes(StandardCharsets.UTF_8);
        }

        String toStr(byte[] bytes) {
            return new String(bytes);
        }

    }

}
