package com.hongcha.pigeon.loadbalance;


import com.hongcha.pigeon.common.service.ServiceAddress;
import com.hongcha.remote.common.spi.SpiLoader;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;


public class LoadBalanceTest {
    @Test
    public void spiTest() {
        List<LoadBalance> loadBalances = SpiLoader.loadAll(LoadBalance.class);
        System.out.println(loadBalances);
    }

    @Test
    public void pollingTest() {
        String applicationName = "pollingTest";
        List<ServiceAddress> serviceAddressList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            serviceAddressList.add(new ServiceAddress("127.0.0.1", 8080 + i));
        }
        LoadBalance<ServiceAddress> loadBalance = SpiLoader.load(LoadBalance.class, "polling");
        for (int i = 0; i < 50; i++) {
            ServiceAddress serviceAddress = loadBalance.choose(applicationName, serviceAddressList);
            System.out.println(serviceAddress);
        }
    }

    @Test
    public void randomTest() {
        String applicationName = "randomTest";
        List<ServiceAddress> serviceAddressList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            serviceAddressList.add(new ServiceAddress("127.0.0.1", 8080 + i));
        }
        LoadBalance<ServiceAddress> loadBalance = SpiLoader.load(LoadBalance.class, "random");
        for (int i = 0; i < 50; i++) {
            ServiceAddress serviceAddress = loadBalance.choose(applicationName, serviceAddressList);
            System.out.println(serviceAddress);
        }
    }

}
