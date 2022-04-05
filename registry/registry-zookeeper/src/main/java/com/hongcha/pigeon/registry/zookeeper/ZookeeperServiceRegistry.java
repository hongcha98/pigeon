package com.hongcha.pigeon.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.hongcha.pigeon.common.error.PigeonException;
import com.hongcha.pigeon.common.service.Service;
import com.hongcha.pigeon.common.service.ServiceAddress;
import com.hongcha.pigeon.registry.AbstractServiceRegistry;
import com.hongcha.pigeon.registry.RegistryConfig;
import com.hongcha.pigeon.registry.RegistryMetadata;
import com.hongcha.remote.common.spi.SpiDescribe;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@SpiDescribe(name = "zookeeper")
public class ZookeeperServiceRegistry extends AbstractServiceRegistry implements Watcher {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);
    private final static String PIGEON_PATH = "/pigeon";
    private final static String NODE = "node";
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private ZooKeeper zooKeeper;
    private Helper helper = new Helper();

    public ZookeeperServiceRegistry(RegistryMetadata registryMetadata, RegistryConfig registryConfig) {
        super(registryMetadata, registryConfig);
    }


    @Override
    protected void init() throws Exception {
        zooKeeper = new ZooKeeper(getRegistryConfig().getAddress(), 5000, this);
        countDownLatch.await();
        helper.create(PIGEON_PATH, "pigeon-rpc", CreateMode.PERSISTENT, false);

    }

    @Override
    protected void doStart() throws Exception {
        RegistryMetadata registryMetadata = getRegistryMetadata();
        /**
         * 注册应用，data为serviceList
         */
        String applicationPath = PIGEON_PATH + "/" + registryMetadata.getApplicationName();
        helper.create(applicationPath, JSON.toJSONString(registryMetadata.getServiceList()), CreateMode.PERSISTENT, true);
        /**
         * 创建应用的临时顺序节点，data为serviceAddress
         */
        String applicationNodePath = applicationPath + "/" + NODE;
        helper.create(applicationNodePath, registryMetadata.getServiceAddress().toString(), CreateMode.EPHEMERAL_SEQUENTIAL, false);
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        countDownLatch.countDown();
    }

    @Override
    public List<ServiceAddress> foundService(String applicationName, Service service) {
        Exception exception = null;
        try {
            String applicationPath = PIGEON_PATH + "/" + applicationName;
            String data = helper.getData(applicationPath);
            List<Service> serviceList = JSON.parseArray(data, Service.class);
            for (Service s : serviceList) {
                if (s.equals(service)) {
                    return zooKeeper.getChildren(applicationPath, null).stream().map(children -> ServiceAddress.parse(helper.getData(applicationPath + "/" + children))).collect(Collectors.toList());
                }
            }

        } catch (Exception e) {
            exception = e;
        }
        throw new PigeonException(applicationName + " not found " + service.getServiceName() + " provider", exception);
    }

    @Override
    public void close() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                LOG.error("zk close error", e);
            }
        }
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


        String create(String path, String data, CreateMode createMode, boolean updateData) {
            try {
                Stat stat = getStat(path);
                if (stat == null) {
                    return zooKeeper.create(path, toByte(data), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                } else {
                    if (updateData) {
                        zooKeeper.setData(path, toByte(data), stat.getVersion());
                    }
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
