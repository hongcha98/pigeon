package com.hongcha.pigeon.loadbalance;

import com.hongcha.pigeon.common.service.ServiceAddress;
import com.hongcha.remote.common.spi.SpiDescribe;

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
