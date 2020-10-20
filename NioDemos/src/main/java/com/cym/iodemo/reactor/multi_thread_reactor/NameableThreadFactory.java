package com.cym.iodemo.reactor.multi_thread_reactor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class NameableThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    NameableThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        namePrefix = name+
                "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r,
                namePrefix + threadNumber.getAndIncrement()
                );

        return t;
    }
}