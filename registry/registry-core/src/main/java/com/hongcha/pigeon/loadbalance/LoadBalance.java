package com.hongcha.pigeon.loadbalance;

import java.util.List;

public interface LoadBalance<T> {
    /**
     * 从列表中选出一个
     *
     * @param applicationName 应用名称
     * @param list            列表
     * @return
     */
    T choose(String applicationName, List<T> list);
}
