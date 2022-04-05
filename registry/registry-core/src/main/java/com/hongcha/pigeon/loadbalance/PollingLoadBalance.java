package com.hongcha.pigeon.loadbalance;

import com.hongcha.pigeon.common.service.ServiceAddress;
import com.hongcha.remote.common.spi.SpiDescribe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询
 */
@SpiDescribe(name = "polling", order = 0)
public class PollingLoadBalance implements LoadBalance<ServiceAddress> {
    private static final Map<String, AtomicInteger> applicationIndexMap = new ConcurrentHashMap<>();

    @Override
    public ServiceAddress choose(String applicationName, List<ServiceAddress> list) {
        AtomicInteger atomicLong = applicationIndexMap.computeIfAbsent(applicationName, name -> new AtomicInteger(0));
        int position = atomicLong.getAndIncrement();
        if (position == Integer.MAX_VALUE) {
            atomicLong.set(0);
            position = 0;
        }
        return list.get(position % list.size());
    }
}
