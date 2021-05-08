package com.hongcha.pigeon.core.remoting;

import java.util.concurrent.atomic.AtomicInteger;

public class PositiveAtomIDGenerate implements IDGenerate {
    private final static AtomicInteger atomic = new AtomicInteger(0);


    @Override
    public int next() {
        return atomic.incrementAndGet();
    }
}
