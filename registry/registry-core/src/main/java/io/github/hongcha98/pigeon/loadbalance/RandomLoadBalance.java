package io.github.hongcha98.pigeon.loadbalance;

import io.github.hongcha98.pigeon.common.service.ServiceAddress;
import io.github.hongcha98.remote.common.spi.SpiDescribe;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
@SpiDescribe(name = "random", order = 1)
public class RandomLoadBalance implements LoadBalance<ServiceAddress> {
    private static final Random RANDOM = new Random();

    @Override
    public ServiceAddress choose(String applicationName, List<ServiceAddress> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }
}
